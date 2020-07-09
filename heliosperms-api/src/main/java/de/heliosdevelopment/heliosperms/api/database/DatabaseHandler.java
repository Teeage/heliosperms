package de.heliosdevelopment.heliosperms.api.database;

import de.heliosdevelopment.heliosperms.api.utils.PermissionGroup;
import de.heliosdevelopment.heliosperms.api.utils.PermissionType;
import de.heliosdevelopment.sqlconnector.SQLClient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseHandler {

    private final SQLClient sqlClient;

    public DatabaseHandler(SQLClient sqlClient) {
        this.sqlClient = sqlClient;
    }

    public boolean bootstrap() {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement groups = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `groups` (`groupId` INT NOT NULL PRIMARY KEY,`name` VARCHAR(99) NOT NULL, `colorCode` VARCHAR(2) NOT NULL, `prefix` VARCHAR(99) NOT NULL, `parentGroup` INT, `defaultGroup` BOOLEAN DEFAULT FALSE) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
            PreparedStatement users = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `users` (`uuid` VARCHAR(36) NOT NULL PRIMARY KEY, `name` VARCHAR(99) NOT NULL, `groupId` INT NOT NULL, `duration` BIGINT NOT NULL, `dateOfReceive` TIMESTAMP NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
            PreparedStatement users_his = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `users_his` (`id` int AUTO_INCREMENT PRIMARY KEY, `uuid` VARCHAR(36) NOT NULL, `groupId` INT NOT NULL, `duration` BIGINT NOT NULL, `editor` VARCHAR(36) NOT NULL, `dateOfReceive` TIMESTAMP, `dateOfChange` TIMESTAMP DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
            PreparedStatement permissions = connection.prepareStatement("CREATE TABLE IF NOT EXISTS  `permissions` (`id` int AUTO_INCREMENT PRIMARY KEY, `type` VARCHAR(99) NOT NULL, `identifier` VARCHAR(99) NOT NULL, `permission` VARCHAR(99) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
            groups.execute();
            users.execute();
            permissions.execute();
            users_his.execute();
            users.close();
            groups.close();
            permissions.close();
            users_his.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getPermissions(String identifier, PermissionType type) {
        List<String> strings = new ArrayList<>();
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `permission` FROM `permissions` WHERE `identifier`=? AND `type`=?");
            preparedStatement.setString(1, identifier);
            preparedStatement.setString(2, type.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next())
                    strings.add(resultSet.getString("permission"));
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return strings;
    }

    public boolean hasPermission(String identifier, PermissionType type, String permission) {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `permission` FROM `permissions` WHERE `identifier`=? AND `type`=? AND `permission`=?");
            preparedStatement.setString(1, identifier);
            preparedStatement.setString(2, type.toString());
            preparedStatement.setString(3, permission);
            ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next())
                    return resultSet.getString("permission").equals(permission);
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    public void addPermission(String identifier, PermissionType type, String permission) {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `permissions` (`type`, `identifier`, `permission`) VALUES (?,?,?)");
            preparedStatement.setString(1, type.toString());
            preparedStatement.setString(2, identifier);
            preparedStatement.setString(3, permission);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removePermission(String identifier, PermissionType type, String permission) {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `permissions` WHERE `identifier`=? AND `type` =? AND `permission`=?");
            preparedStatement.setString(1, identifier);
            preparedStatement.setString(2, type.toString());
            preparedStatement.setString(3, permission);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addUser(UUID uuid, String name, int groupId, Long duration) {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `users` (`uuid`, `name`, `groupId`, `duration`, `dateOfReceive`) VALUES (?,?,?,?,NOW())");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, groupId);
            preparedStatement.setString(4, duration.toString());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addHisEntry(UUID uuid, int groupId, Long duration, String editor, Timestamp dateOfRecTimestamp) {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `users_his` (`uuid`, `groupId`, `duration`, `editor`, `dateOfReceive`, `dateOfChange`) VALUES (?,?,?,?,?,NOW())");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setInt(2, groupId);
            preparedStatement.setString(3, duration.toString());
            preparedStatement.setString(4, editor);
            preparedStatement.setTimestamp(5, dateOfRecTimestamp);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUser(UUID uuid, String name, int groupId, Long duration) {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `users` SET `name`=?, `groupId`=?, `duration`=?, `dateOfReceive`=NOW() WHERE `uuid`=?");
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, groupId);
            preparedStatement.setString(3, duration.toString());
            preparedStatement.setString(4, uuid.toString());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeUser(UUID uuid) {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `users` WHERE `uuid`=?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getGroup(UUID uuid) {
        int groupId = -1;
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `groupId` FROM `users` WHERE `uuid`=?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next())
                    groupId = resultSet.getInt("groupId");
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groupId;
    }

    public Long getExpiration(UUID uuid) {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `duration` FROM `users` WHERE `uuid`=?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next())
                    return resultSet.getLong("duration");
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getUsersByGroup(int groupId) {
        List<String> perms = new ArrayList<>();
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `name` FROM `users` WHERE `groupId`=?");
            preparedStatement.setInt(1, groupId);
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next())
                    perms.add(resultSet.getString("name"));
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return perms;
    }

    public void addGroup(int groupId, String name, String colorCode, String prefix, Integer parentGroup, boolean defaultGroup) {
        if (groupId < 0 || groupId > 99)
            throw new IllegalArgumentException("[HeliosPerms] The groupId must be in the range between 1 and 99.");
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `groups` (`groupId`, `name`, `colorCode`, `prefix`, `parentGroup`, `defaultGroup`) VALUES (?,?,?,?,?,?)");
            preparedStatement.setInt(1, groupId);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, colorCode);
            preparedStatement.setString(4, prefix);
            preparedStatement.setObject(5, parentGroup);
            preparedStatement.setBoolean(6, defaultGroup);
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
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `groups`");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("groupId");
                groups.add(new PermissionGroup(id, resultSet.getString("name"), resultSet.getString("prefix"),
                        resultSet.getString("colorCode"),
                        resultSet.getInt("parentGroup"), getPermissions(String.valueOf(id), PermissionType.GROUP), resultSet.getBoolean("defaultGroup")));
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    public UUID getUuidByName(String name) {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `users` WHERE `name`=?");
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next())
                    return UUID.fromString(resultSet.getString("uuid"));
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Timestamp getLastDateOfChange(UUID uuid) {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `dateOfReceive` FROM `users` WHERE `uuid`=?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return resultSet.getTimestamp("dateOfReceive");
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getDefaultGroup() {
        try (Connection connection = this.sqlClient.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `groupId` FROM `groups` WHERE `defaultGroup`=?");
            preparedStatement.setBoolean(1, true);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return resultSet.getInt("groupId");
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
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
