package ptithcm.bean;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Centralizes all database calls through stored procedures.
 */
public final class StoredProcedure {
    private StoredProcedure() {
    }

    public static List<Map<String, Object>> query(JdbcTemplate jdbc, String name, Object... args) {
        Object[] params = getParameters(args);
        return jdbc.queryForList(sql(name, params.length), params);
    }

    public static <T> T object(JdbcTemplate jdbc, String name, Class<T> type, Object... args) {
        Object[] params = getParameters(args);
        return jdbc.queryForObject(sql(name, params.length), type, params);
    }

    public static int update(JdbcTemplate jdbc, String name, Object... args) {
        Object[] params = getParameters(args);
        return jdbc.update(sql(name, params.length), params);
    }

    private static Object[] getParameters(Object[] args) {
        if (args == null) {
            return new Object[]{ null };
        }
        return args;
    }

    private static String sql(String name, int parameterCount) {
        StringBuilder builder = new StringBuilder("EXEC dbo.").append(name);
        if (parameterCount > 0) {
            builder.append(' ');
            for (int i = 0; i < parameterCount; i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append('?');
            }
        }
        return builder.toString();
    }
}
