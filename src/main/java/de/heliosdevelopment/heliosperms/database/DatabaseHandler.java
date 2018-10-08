package de.heliosdevelopment.heliosperms.database;

import de.heliosdevelopment.heliosperms.utils.PermissionGroup;
import de.heliosdevelopment.heliosperms.utils.PermissionType;
import de.heliosdevelopment.sqlconnector.SQLClient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {

    private final SQLClient sqlClient;

    public DatabaseHandler(SQLClient sqlClient) {
        this.sqlClient = sqlClient;
    }

    public boolean bootstrap() {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement groups = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `groups` (`groupId` INT(9) NOT NULL,`name` VARCHAR(99) NOT NULL, `colorCode` VARCHAR(99) NOT NULL, `prefix` VARCHAR(99) NOT NULL, `parentGroup` INT(9) NOT NULL, PRIMARY KEY (`groupId`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
            groups.execute();
            groups.close();
            PreparedStatement users = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `users` (`uuid` VARCHAR(99) NOT NULL, `name` VARCHAR(99) NOT NULL, `groupId` INT(9) NOT NULL, `duration` VARCHAR(99) NOT NULL, PRIMARY KEY (`uuid`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
            users.execute();
            users.close();
            PreparedStatement permissions = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `permissions` (`type` VARCHAR(99) NOT NULL, `name` VARCHAR(99) NOT NULL NOT NULL, `permission` VARCHAR(99) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
            permissions.execute();
            permissions.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getPermissions(String name, PermissionType type) {
        List<String> strings = new ArrayList<>();
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `permission` FROM `permissions` WHERE `name`=? AND `type`=?");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, type.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet != null)
                while (resultSet.next())
                    strings.add(resultSet.getString("permission"));
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return strings;
    }

    public boolean hasPermission(String name, PermissionType type, String permission) {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `permission` FROM `permissions` WHERE `name`=? AND `type`=? AND `permission`=?");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, type.toString());
            preparedStatement.setString(3, permission);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet != null)
                if (resultSet.next())
                    if (resultSet.getString("permission").equals(permission))
                        return true;
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    public void addPermission(String name, PermissionType type, String permission) {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `permissions` (`type`, `name`, `permission`) VALUES (?,?,?)");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, type.toString());
            preparedStatement.setString(3, permission);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removePermission(String name, PermissionType type, String permission) {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `permissions` WHERE `name`=? AND `type` =? AND `permission`=?");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, type.toString());
            preparedStatement.setString(3, permission);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addUser(String uuid, String name, int groupId, Long duration) {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `users` (`uuid`, `name`, `groupId`, `duration`) VALUES VALUES (?,?,?,?)");
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, groupId);
            preparedStatement.setString(4, duration.toString());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUser(String uuid, String name, int groupId, Long duration) {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `users` SET `name`=?, `groupId`=?, `duration`=?' WHERE `uuid`=?");
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, groupId);
            preparedStatement.setString(3, duration.toString());
            preparedStatement.setString(4, uuid);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeUser(String uuid) {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `users` WHERE `uuid`=?");
            preparedStatement.setString(1, uuid);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getGroup(String uuid) {
        int groupId = -1;
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `groupId` FROM `users` WHERE `uuid`=?");
            preparedStatement.setString(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet != null)
                if (resultSet.next())
                    groupId = resultSet.getInt("groupId");
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groupId;
    }

    public String getExpiration(String uuid) {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `duration` FROM `users` WHERE `uuid`=?");
            preparedStatement.setString(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet != null)
                if (resultSet.next())
                    return resultSet.getString("duration");
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getUsers(int groupId) {
        List<String> perms = new ArrayList<>();
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `name` FROM `users` WHERE `groupId`=?");
            preparedStatement.setInt(1, groupId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet != null)
                while (resultSet.next())
                    perms.add(resultSet.getString("name"));
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return perms;
    }

    public void addGroup(int groupId, String name, String colorCode, String prefix, int parentGroup) {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `groups` (`groupId`, `name`, `colorCode`, `prefix`, `parentGroup`) VALUES (?,?,?,?,?)");
            preparedStatement.setInt(1, groupId);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, colorCode);
            preparedStatement.setString(4, prefix);
            preparedStatement.setInt(5, parentGroup);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeGroup(int groupId) {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `groups` WHERE `groupId`=?");
            preparedStatement.setInt(1, groupId);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PermissionGroup> getGroups() {
        List<PermissionGroup> groups = new ArrayList<>();
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SSELECT * FROM `groups`");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet != null)
                while (resultSet.next()) {
                    Integer id = resultSet.getInt("groupId");
                    groups.add(new PermissionGroup(id, resultSet.getString("name"), resultSet.getString("prefix"),
                            resultSet.getString("colorCode"),
                            resultSet.getInt("parentGroup"), getPermissions(id.toString(), PermissionType.GROUP)));
                }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    public String getUuid(String name) {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `users` WHERE `name`=?");
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet != null)
                if (resultSet.next())
                    return resultSet.getString("uuid");
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateGroup(int groupId, String key, String value) {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `groups` SET ?=? WHERE `groupId`=?");
            preparedStatement.setString(1, key);
            preparedStatement.setString(2, value);
            preparedStatement.setInt(3, groupId);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SQLClient getSqlClient() {
        return sqlClient;
    }
}
