package de.heliosdevelopment.heliosperms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.heliosdevelopment.heliosperms.utils.PermissionGroup;
import de.heliosdevelopment.heliosperms.utils.PermissionType;

public class MySQL {

    private Connection connection = null;
    private final String host;
    private final String port;
    private final String database;
    private final String user;
    private final String password;

    public MySQL(String host, String port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
        connect();
        createTable();
    }

    private void connect() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password);
        } catch (ClassNotFoundException e) {
            System.out.println("HeliosPerms Treiber nicht gefunden");
        } catch (SQLException e) {
            System.out.println("HeliosPerms Verbindung nicht moeglich");
            System.out.println("HeliosPerms SQLException: " + e.getMessage());
            System.out.println("HeliosPerms SQLState: " + e.getSQLState());
            System.out.println("HeliosPerms VendorError: " + e.getErrorCode());
        }

    }

    public void close() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("HeliosPerms Fehler: " + e.getMessage());
            }
        }
    }

    private boolean isConnected() {
        return connection != null;
    }

    private void createTable() {
        updateSQL("CREATE TABLE IF NOT EXISTS `groups` (`groupId` INT(9) NOT NULL,"
                + " `name` VARCHAR(99) NOT NULL, `colorCode` VARCHAR(99) NOT NULL, `prefix` VARCHAR(99) NOT NULL,"
                + " `parentGroup` INT(9) NOT NULL)" + " ENGINE=InnoDB DEFAULT CHARSET=latin1;");

        updateSQL(
                "CREATE TABLE IF NOT EXISTS `users` (`uuid` VARCHAR(99) NOT NULL, `name` VARCHAR(99) NOT NULL, `groupId` INT(9) NOT NULL, `duration` VARCHAR(99) NOT NULL)"
                        + " ENGINE=InnoDB DEFAULT CHARSET=latin1;");

        updateSQL("CREATE TABLE IF NOT EXISTS `permissions` (`type` VARCHAR(99) NOT NULL, `name` VARCHAR(99) NOT NULL"
                + ",`permission` VARCHAR(99) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
        /*
         * GroupID, Permission UUID, Permission
		 * 
		 * Users UUID, GroupID
		 * 
		 * Groups GroupID, Name, ColorCode, Prefix, parentGroup
		 * 
		 * 
		 * 
		 */

    }

    private void updateSQL(String statment) {
        Statement statement;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(statment);
        } catch (SQLException e) {
            System.out.println("HeliosPerms MySQL-UpdateSQL:" + e.getMessage());
        }
    }

    private ResultSet select(String sql) {
        if (connection != null) {
            Statement query;
            try {
                query = connection.createStatement();
                return query.executeQuery(sql);

            } catch (SQLException e) {
                System.out.println("HeliosPerms MySQL-Select:" + e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<String> getPermissions(String name, PermissionType type) {
        ResultSet result = select("SELECT `permission` FROM `permissions` WHERE `name` = '" + name + "' AND `type` = '"
                + type.toString() + "'");
        List<String> perms = new ArrayList<>();
        if (result == null) {
            return perms;
        }

        try {
            while (result.next()) {
                String s = result.getString("permission");
                System.out.println(s);
                perms.add(s);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return perms;
    }

    public void addPermission(String name, PermissionType type, String permission) {
        updateSQL("INSERT INTO `permissions` (`type`, `name`, `permission`) VALUES ('" + type.toString() + "', '" + name
                + "','" + permission + "')");

    }

    public boolean hasPermission(String name, PermissionType type, String permission) {
        ResultSet result = select("SELECT `permission` FROM `permissions` WHERE `name` = '" + name + "' AND `type` = '"
                + type.toString() + "'AND `permission` = '" + permission + "'");
        if (result == null) {
            return false;
        }

        try {
            while (result.next()) {
                if (result.getString("permission").equals(permission))
                    return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;

    }

    public void removePermission(String name, PermissionType type, String permission) {
        updateSQL("DELETE FROM `permissions` WHERE `name`='" + name + "' AND `type` = '" + type.toString()
                + "'AND `permission` = '" + permission + "'");
    }

    public void addUser(String uuid, String name, int groupId, Long duration) {
        updateSQL("INSERT INTO `users` (`uuid`, `name`, `groupId`, `duration`) VALUES ('" + uuid + "', '" + name + "','"
                + groupId + "','" + duration.toString() + "')");
    }

    public void updateUser(String uuid, String name, int groupId, Long duration) {
        updateSQL("UPDATE `users` SET `name`='" + name + "', `groupId`='" + groupId + "', `duration`='" + duration + "' WHERE `uuid`='" + uuid + "'");
    }

    public void removeUser(String uuid) {
        updateSQL("DELETE FROM `users` WHERE `uuid`='" + uuid + "'");
    }

    public int getGroup(String uuid) {
        ResultSet result = select("SELECT `groupId` FROM `users` WHERE `uuid` = '" + uuid + "'");
        int groupId = -1;
        if (result == null) {
            return groupId;
        }
        try {
            while (result.next()) {
                groupId = result.getInt("groupId");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return groupId;
    }

    public String getExpiration(String uuid) {
        ResultSet result = select("SELECT `duration` FROM `users` WHERE `uuid` = '" + uuid + "'");
        String expiration = null;
        if (result == null) {
            return null;
        }
        try {
            while (result.next()) {
                expiration = result.getString("duration");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return expiration;
    }

    public List<String> getUsers(int groupId) {
        ResultSet result = select("SELECT `name` FROM `users` WHERE `groupId` = '" + groupId + "'");
        List<String> perms = new ArrayList<>();
        if (result == null) {
            return perms;
        }

        try {
            while (result.next()) {
                perms.add(result.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return perms;
    }

    public void addGroup(int groupId, String name, String colorCode, String prefix, int parentGroup) {
        updateSQL("INSERT INTO `groups` (`groupId`, `name`, `colorCode`, `prefix`, `parentGroup`) VALUES ('" + groupId
                + "', '" + name + "','" + colorCode + "', '" + prefix + "','" + parentGroup + "')");
    }

    public void removeGroup(int groupId) {
        updateSQL("DELETE FROM `groups` WHERE `groupId`='" + groupId + "'");
    }

//	public Group getGroup(int groupId) {
//		return null;
//	}

    public List<PermissionGroup> getGroups() {
        ResultSet result = select("SELECT * FROM `groups`");
        List<PermissionGroup> groups = new ArrayList<>();
        if (result == null) {
            return groups;
        }

        try {
            while (result.next()) {
                Integer id = result.getInt("groupId");
                groups.add(new PermissionGroup(id, result.getString("name"), result.getString("prefix"),
                        result.getString("colorCode"),
                        result.getInt("parentGroup"), getPermissions(id.toString(), PermissionType.GROUP)));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return groups;
    }

    public String getUuid(String name) {

        try {
            ResultSet result = select("SELECT `uuid` FROM `users` WHERE `name`='" + name + "'");
            assert result != null;
            while (result.next()) {
                return result.getString("uuid");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

}