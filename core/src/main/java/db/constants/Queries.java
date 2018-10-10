package db.constants;

/**
 * Created by Makoiedov.H on 9/22/2017.
 */

/**
 * Class container which contains sql queries for loading
 * metainformation of specific object
 */

public final class Queries {

    //////Queries for lazy loading
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String SCHEMA_QUERY = "SELECT catalog_name, default_character_set_name," +
            "default_collation_name, " +
            "schema_name as name, sql_path " +
            "FROM information_schema.schemata " +
            "WHERE schema_name = ?; ";

    public static final String TABLES_QUERY = "SELECT auto_increment, avg_row_length, " +
            "checksum, check_time, create_options, create_time, " +
            "data_free, data_length, engine, index_length, " +
            "max_data_length, row_format, table_catalog, table_collation, " +
            "table_comment, table_name as name, table_rows, table_schema, table_type, " +
            "update_time, version " +
            "FROM information_schema.TABLES " +
            "WHERE table_schema = ? AND table_name = ? AND table_type <> 'VIEW'; ";
    public static final String TABLES_NAME_QUERY = "SELECT table_name " +
            "FROM information_schema.TABLES " +
            "WHERE table_schema = ? AND table_type <> 'VIEW'; ";


    public static final String VIEWS_QUERY = "SELECT character_set_client, check_option, " +
            "collation_connection, definer, is_updatable, security_type, " +
            "table_catalog, table_name as name, table_schema, view_definition " +
            "FROM information_schema.VIEWS " +
            "WHERE table_schema = ? AND table_name = ?; ";
    public static final String VIEWS_NAME_QUERY = "SELECT table_name " +
            "FROM information_schema.VIEWS " +
            "WHERE table_schema = ?; ";


    public static final String COLUMNS_QUERY = "SELECT character_maximum_length, character_octet_length, " +
            "character_set_name, collation_name, column_comment, column_default, " +
            "column_key, column_name as name, column_type, data_type, extra, is_nullable, " +
            "numeric_precision, numeric_scale, ordinal_position, privileges, table_catalog, " +
            "table_name, table_schema " +
            "FROM information_schema.COLUMNS " +
            "WHERE table_schema = ? AND table_name = ? AND column_name = ?; ";
    public static final String COLUMNS_NAME_QUERY = "SELECT column_name as name " +
            "FROM information_schema.COLUMNS " +
            "WHERE table_schema = ? AND table_name = ?; ";


    public static final String PRIMARY_KEYS_QUERY = "SELECT column_name as name, constraint_catalog, constraint_name, " +
            "constraint_schema, ordinal_position, position_in_unique_constraint, " +
            "referenced_column_name, referenced_table_name, referenced_table_schema, " +
            "table_catalog, table_name, table_schema " +
            "FROM information_schema.key_column_usage " +
            "WHERE constraint_name = 'PRIMARY' AND table_schema = ? AND table_name = ? AND column_name = ?; ";
    public static final String PRIMARY_KEYS_NAME_QUERY = "SELECT column_name as name " +
            "FROM information_schema.key_column_usage " +
            "WHERE constraint_name = 'PRIMARY' AND table_schema = ? AND table_name = ? ; ";


    public static final String FOREIGN_KEYS_QUERY = "SELECT fk.column_name, fk.constraint_catalog, fk.constraint_name as name, " +
            "fk.constraint_schema, fk.ordinal_position, fk.position_in_unique_constraint, " +
            "fk.referenced_column_name, fk.referenced_table_name, fk.referenced_table_schema, " +
            "fk.table_catalog, fk.table_name, fk.table_schema, c.delete_rule, c.update_rule " +
            "FROM information_schema.key_column_usage fk, information_schema.referential_constraints c " +
            "WHERE fk.referenced_table_schema IS NOT NULL AND fk.table_schema = ? AND fk.table_name = ? AND fk.constraint_name = ? AND " +
            "fk.constraint_name = c.constraint_name;";

    public static final String FOREIGN_KEYS_NAME_QUERY = "SELECT constraint_name " +
            "FROM information_schema.key_column_usage " +
            "WHERE referenced_table_schema IS NOT NULL AND table_schema = ? AND table_name = ?; ";

    //query has complex syntax for loading multiple-columns indexes
    public static final String INDEXES_QUERY = "SELECT cardinality, collation, column_name, comment, " +
            "index_comment, index_name as name, index_schema, index_type, non_unique, " +
            "nullable, packed, seq_in_index, sub_part, table_catalog, " +
            "table_name, table_schema, " +
            "GROUP_CONCAT(column_name ORDER BY seq_in_index) AS `columns` " +
            "FROM information_schema.statistics " +
            "WHERE table_schema = ? AND table_name = ? AND index_name = ?; ";
    public static final String INDEXES_NAME_QUERY = "SELECT index_name "  +
            "FROM information_schema.statistics " +
            "WHERE table_schema = ? AND table_name = ? AND index_name <> 'primary' ";


    public static final String TRIGGERS_QUERY = "SELECT action_condition, action_order, action_orientation, " +
            "action_reference_new_row, action_reference_new_table, action_reference_old_row, " +
            "action_reference_old_table, action_statement, action_timing, character_set_client, " +
            "collation_connection, created, database_collation, definer, event_manipulation, " +
            "event_object_catalog, event_object_schema, event_object_table, sql_mode, " +
            "trigger_catalog, trigger_name as name, trigger_schema " +
            "FROM information_schema.TRIGGERS " +
            "WHERE event_object_schema = ? AND EVENT_OBJECT_TABLE = ? AND trigger_name = ?; ";
    public static final String TRIGGERS_NAME_QUERY = "SELECT trigger_name as name " +
            "FROM information_schema.TRIGGERS " +
            "WHERE event_object_schema = ? AND EVENT_OBJECT_TABLE = ?; ";


    public static final String FUNCTIONS_QUERY = "SELECT character_maximum_length, character_octet_length, character_set_client, " +
            "character_set_name, collation_connection, collation_name, created, database_collation, " +
            "data_type, definer, dtd_identifier, external_language, external_name, " +
            "is_deterministic, last_altered, numeric_precision, numeric_scale, parameter_style, " +
            "routine_body, routine_catalog, routine_comment, routine_definition, routine_name as name, " +
            "routine_schema, routine_type, security_type, specific_name, sql_data_access, sql_mode, sql_path " +
            "FROM information_schema.routines " +
            "WHERE routine_schema = ? AND routine_name = ?  AND routine_type = 'FUNCTION'; ";
    public static final String FUNCTIONS_NAME_QUERY = "SELECT routine_name " +
            "FROM information_schema.routines " +
            "WHERE routine_schema = ? AND routine_type = 'FUNCTION'; ";


    public static final String PROCEDURES_QUERY = "SELECT character_maximum_length, character_octet_length, character_set_client, " +
            "character_set_name, collation_connection, collation_name, created, database_collation, " +
            "data_type, definer, dtd_identifier, external_language, external_name, " +
            "is_deterministic, last_altered, numeric_precision, numeric_scale, parameter_style, " +
            "routine_body, routine_catalog, routine_comment, routine_definition, routine_name as name, " +
            "routine_schema, routine_type, security_type, specific_name, sql_data_access, sql_mode, sql_path " +
            "FROM information_schema.routines " +
            "WHERE routine_schema = ? AND routine_name = ? AND routine_type = 'PROCEDURE' ";
    public static final String PROCEDURES_NAME_QUERY = "SELECT routine_name " +
            "FROM information_schema.routines " +
            "WHERE routine_schema = ? AND routine_type = 'PROCEDURE'; ";

    public static final String PARAMETERS_QUERY = "SELECT character_maximum_length, character_octet_length, " +
            "character_set_name, collation_name, data_type, dtd_identifier, numeric_precision, numeric_scale, " +
            "ordinal_position, parameter_mode, parameter_name as name, routine_type, specific_catalog, specific_name, specific_schema " +
            "FROM information_schema.parameters " +
            "WHERE specific_schema = ? AND specific_name = ? AND parameter_name = ? ;";
    public static final String PARAMETERS_NAME_QUERY = "SELECT parameter_name " +
            "FROM information_schema.PARAMETERS " +
            "WHERE specific_schema = ? AND specific_name = ? AND parameter_name IS NOT NULL";

    public static final String FUNCTION_RETURN_QUERY = "SELECT character_maximum_length, character_octet_length, " +
            "character_set_name, collation_name, data_type, dtd_identifier, numeric_precision, numeric_scale, " +
            "ordinal_position, parameter_mode, parameter_name as name, routine_type, specific_catalog, specific_name, specific_schema " +
            "FROM information_schema.parameters " +
            "WHERE specific_schema = ? AND specific_name = ? AND parameter_name IS NULL ;";

    public static final String CONSTRAINT_QUERY = "SELECT constraint_catalog, constraint_name, " +
            "constraint_schema, delete_rule, match_option, referenced_table_name, table_name, " +
            "unique_constraint_catalog, unique_constraint_name, unique_constraint_schema, update_rule " +
            "FROM information_schema.referential_constraints " +
            "WHERE constraint_schema = ? AND table_name = ? AND constraint_name = ? ;";
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    //////////Queries for full loading
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String ALL_TABLES_QUERY =  "SELECT auto_increment, avg_row_length, " +
            "checksum, check_time, create_options, create_time, " +
            "data_free, data_length, engine, index_length, " +
            "max_data_length, row_format, table_catalog, table_collation, " +
            "table_comment, table_name as name, table_rows, table_schema, table_type, " +
            "update_time, version " +
            "FROM information_schema.TABLES " +
            "WHERE table_schema = ? AND table_type <> 'VIEW'; ";

    public static final String ALL_VIEWS_QUERY = "SELECT character_set_client, check_option, " +
            "collation_connection, definer, is_updatable, security_type, " +
            "table_catalog, table_name as name, table_schema, view_definition " +
            "FROM information_schema.VIEWS " +
            "WHERE table_schema = ? ";


    public static final String ALL_PROCEDURES_QUERY = "SELECT character_maximum_length, character_octet_length, character_set_client, " +
            "character_set_name, collation_connection, collation_name, created, database_collation, " +
            "data_type, definer, dtd_identifier, external_language, external_name, " +
            "is_deterministic, last_altered, numeric_precision, numeric_scale, parameter_style, " +
            "routine_body, routine_catalog, routine_comment, routine_definition, routine_name as name, " +
            "routine_schema, routine_type, security_type, specific_name, sql_data_access, sql_mode, sql_path " +
            "FROM information_schema.routines " +
            "WHERE routine_schema = ? AND routine_type = 'PROCEDURE' ";


    public static final String ALL_FUNCTIONS_QUERY = "SELECT character_maximum_length, character_octet_length, character_set_client, " +
            "character_set_name, collation_connection, collation_name, created, database_collation, " +
            "data_type, definer, dtd_identifier, external_language, external_name, " +
            "is_deterministic, last_altered, numeric_precision, numeric_scale, parameter_style, " +
            "routine_body, routine_catalog, routine_comment, routine_definition, routine_name as name, " +
            "routine_schema, routine_type, security_type, specific_name, sql_data_access, sql_mode, sql_path " +
            "FROM information_schema.routines " +
            "WHERE routine_schema = ? AND routine_type = 'FUNCTION'; ";


    public static final String ALL_COLUMNS_QUERY ="SELECT character_maximum_length, character_octet_length, " +
            "character_set_name, collation_name, column_comment, column_default, " +
            "column_key, column_name as name, column_type, data_type, extra, is_nullable, " +
            "numeric_precision, numeric_scale, ordinal_position, privileges, table_catalog, " +
            "table_name, table_schema " +
            "FROM information_schema.COLUMNS " +
            "WHERE table_schema = ?; ";


    public static final String ALL_PRIMARY_KEYS_QUERY = "SELECT column_name as name, constraint_catalog, constraint_name, " +
            "constraint_schema, ordinal_position, position_in_unique_constraint, " +
            "referenced_column_name, referenced_table_name, referenced_table_schema, " +
            "table_catalog, table_name, table_schema " +
            "FROM information_schema.key_column_usage " +
            "WHERE constraint_name = 'PRIMARY' AND table_schema = ?; ";


    public static final String ALL_FOREIGN_KEYS_QUERY = "SELECT fk.column_name, fk.constraint_catalog, fk.constraint_name as name, " +
            "fk.constraint_schema, fk.ordinal_position, fk.position_in_unique_constraint, " +
            "fk.referenced_column_name, fk.referenced_table_name, fk.referenced_table_schema, " +
            "fk.table_catalog, fk.table_name, fk.table_schema, c.delete_rule, c.update_rule " +
            "FROM information_schema.key_column_usage fk, information_schema.referential_constraints c " +
            "WHERE fk.referenced_table_schema IS NOT NULL AND fk.table_schema = ? AND " +
            "fk.constraint_name = c.constraint_name ;";


    public static final String ALL_INDEXES_QUERY = "SELECT cardinality, collation, column_name AS 'columns', comment, " +
            "index_comment, index_name as name, index_schema, index_type, non_unique, " +
            "nullable, packed, seq_in_index, sub_part, table_catalog, " +
            "table_name, table_schema " +
            "FROM information_schema.statistics " +
            "WHERE table_schema = ? AND index_name <> 'PRIMARY'; ";


    public static final String ALL_TRIGGERS_QUERY = "SELECT action_condition, action_order, action_orientation, " +
            "action_reference_new_row, action_reference_new_table, action_reference_old_row, " +
            "action_reference_old_table, action_statement, action_timing, character_set_client, " +
            "collation_connection, created, database_collation, definer, event_manipulation, " +
            "event_object_catalog, event_object_schema, event_object_table, sql_mode, " +
            "trigger_catalog, trigger_name as name, trigger_schema " +
            "FROM information_schema.TRIGGERS " +
            "WHERE event_object_schema = ?; ";


    public static final String ALL_PARAMETERS_QUERY = "SELECT character_maximum_length, character_octet_length, " +
            "character_set_name, collation_name, data_type, dtd_identifier, numeric_precision, numeric_scale, " +
            "ordinal_position, parameter_mode, parameter_name as name, routine_type, specific_catalog, specific_name, specific_schema " +
            "FROM information_schema.parameters " +
            "WHERE specific_schema = ? AND parameter_name is NOT NULL;";


    public static final String ALL_FUNCTION_RETURNS_QUERY = "SELECT character_maximum_length, character_octet_length, " +
            "character_set_name, collation_name, data_type, dtd_identifier, numeric_precision, numeric_scale, " +
            "ordinal_position, parameter_mode, parameter_name as name, routine_type, specific_catalog, specific_name, specific_schema " +
            "FROM information_schema.parameters " +
            "WHERE specific_schema = ? AND parameter_name IS NULL ;";

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
