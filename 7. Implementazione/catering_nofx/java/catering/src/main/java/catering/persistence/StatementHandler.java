package catering.persistence;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface StatementHandler {
    void handle(PreparedStatement ps) throws SQLException;
}
