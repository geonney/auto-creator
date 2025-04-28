package studio.geonlee.auto_creator.common.util;

import studio.geonlee.auto_creator.common.enumeration.DatabaseType;
import studio.geonlee.auto_creator.common.record.FieldMetadata;
import studio.geonlee.auto_creator.context.DatabaseContext;
import studio.geonlee.auto_creator.ui.frame.MainFrame;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author GEON
 * @since 2025-04-28
 **/
public class DatabaseMetaReader {

    public static List<FieldMetadata> readTableFields(String schema, String tableName, DatabaseType dbType) {
        List<FieldMetadata> fields = new ArrayList<>();

        try {
            Connection conn = DatabaseContext.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();

            // ✅ primary key 컬럼 조회
            List<String> pkColumns = new ArrayList<>();
            try (ResultSet pkRs = metaData.getPrimaryKeys(null, schema, tableName)) {
                while (pkRs.next()) {
                    pkColumns.add(pkRs.getString("COLUMN_NAME"));
                }
            }

            // ✅ 컬럼 정보 조회
            try (ResultSet columns = metaData.getColumns(null, schema, tableName, "%")) {
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String dbTypeName = columns.getString("TYPE_NAME");
                    boolean nullable = "YES".equalsIgnoreCase(columns.getString("IS_NULLABLE"));
                    int length = columns.getInt("COLUMN_SIZE");
                    String columnDefault = columns.getString("COLUMN_DEF");
                    String comment = columns.getString("REMARKS");

                    fields.add(FieldMetadata.of(
                            dbType,
                            columnName,
                            dbTypeName,
                            pkColumns.contains(columnName),
                            nullable,
                            length,
                            columnDefault,
                            comment
                    ));
                }
            }

        } catch (Exception ex) {
            MainFrame.log("❌ 테이블 메타데이터 읽기 실패: " + ex.getMessage());
        }
        return fields;
    }
}