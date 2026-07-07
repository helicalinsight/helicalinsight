const defaultColumns = [
    {
      title: 'Name',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: 'Description',
      dataIndex: 'description',
      key: 'description',
    },
    {
      title: 'Type',
      dataIndex: 'type',
      key: 'type',
    },
    {
      title: 'Default',
      dataIndex: 'defaultValue',
      key: 'defaultValue',
    },
]

const csvConf = {
    title: "CSV Configuration",
    columns: defaultColumns,
    data: [
        {
          key: '1',
          name: 'all_varchar',
          description: 'Option to skip type detection for CSV parsing and assume all columns to be of type VARCHAR.',
          type: 'BOOL',
          defaultValue: 'false',
        },
        {
          key: '2',
          name: 'allow_quoted_nulls',
          description: 'Option to allow the conversion of quoted values to NULL values',
          type: 'BOOL',
          defaultValue: 'true',
        },
        {
          key: '3',
          name: 'auto_detect',
          description: 'Enables auto detection of CSV parameters.',
          type: 'BOOL',
          defaultValue: 'true',
        },
        {
          key: '4',
          name: 'auto_type_candidates',
          description: 'Allows specifying types for CSV column type detection. VARCHAR is always included as a fallback.',
          type: 'TYPE[]',
          defaultValue: 'default types',
        },
        {
          key: '5',
          name: 'columns',
          description: `Specifies the column names and types within the CSV file (e.g., {'col1': 'INTEGER', 'col2': 'VARCHAR'}). Implies no auto detection.`,
          type: 'STRUCT',
          defaultValue: '(empty)',
        },
        {
          key: '6',
          name: 'compression',
          description: `The compression type for the file. Auto-detected by default (e.g., t.csv.gz -> gzip, t.csv -> none).`,
          type: 'VARCHAR',
          defaultValue: 'auto',
        },
        {
          key: '7',
          name: 'dateformat',
          description: 'Specifies the date format to use when parsing dates. See Date Format example below.',
          type: 'VARCHAR',
          defaultValue: '(empty)',
        },
        {
          key: '8',
          name: 'decimal_separator',
          description: 'The decimal separator of numbers.',
          type: 'VARCHAR',
          defaultValue: '.',
        },
        {
          key: '9',
          name: 'delim or sep',
          description: 'Specifies the character that separates columns within each row.',
          type: 'VARCHAR',
          defaultValue: ',',
        },
        {
          key: '10',
          name: 'escape',
          description: 'Specifies the string used to escape data character sequences matching the quote value.',
          type: 'VARCHAR',
          defaultValue: '"',
        },
        {
          key: '11',
          name: 'filename',
          description: 'Whether an extra filename column should be included in the result.',
          type: 'BOOL',
          defaultValue: 'false',
        },
        {
          key: '12',
          name: 'force_not_null',
          description: 'Do not match specified columns’ values against the NULL string.',
          type: 'VARCHAR[]',
          defaultValue: '[]',
        },
        {
          key: '13',
          name: 'header',
          description: 'Specifies that the file contains a header line with the names of each column.',
          type: 'BOOL',
          defaultValue: 'false',
        },
        {
          key: '14',
          name: 'hive_partitioning',
          description: 'Whether or not to interpret the path as a Hive partitioned path.',
          type: 'BOOL',
          defaultValue: 'false',
        },
        {
          key: '15',
          name: 'ignore_errors',
          description: 'Option to ignore any parsing errors encountered and ignore rows with errors.',
          type: 'BOOL',
          defaultValue: 'false',
        },
        {
          key: '16',
          name: 'max_line_size',
          description: 'The maximum line size in bytes.',
          type: 'BIGINT',
          defaultValue: '2097152',
        },
        {
          key: '17',
          name: 'names',
          description: 'The column names as a list.',
          type: 'VARCHAR[]',
          defaultValue: '(empty)',
        },
        {
          key: '18',
          name: 'new_line',
          description: "Set the new line character(s) in the file. Options are '\\r','\\n', or '\\r\\n'.",
          type: 'VARCHAR',
          defaultValue: '(empty)',
        },
        {
          key: '19',
          name: 'normalize_names',
          description: 'Whether column names should be normalized by removing non-alphanumeric characters.',
          type: 'BOOL',
          defaultValue: 'false',
        },
        {
          key: '20',
          name: 'null_padding',
          description: 'Pads remaining columns on the right with null values if a row lacks columns.',
          type: 'BOOL',
          defaultValue: 'false',
        },
        {
          key: '21',
          name: 'nullstr',
          description: 'Specifies the string or list of strings that represent a NULL value.',
          type: 'VARCHAR or VARCHAR[]',
          defaultValue: '(empty)',
        },
        {
          key: '22',
          name: 'parallel',
          description: 'Whether or not the parallel CSV reader is used.',
          type: 'BOOL',
          defaultValue: 'true',
        },
        {
          key: '23',
          name: 'quote',
          description: 'Specifies the quoting string to be used when a data value is quoted.',
          type: 'VARCHAR',
          defaultValue: '"',
        },
        {
          key: '24',
          name: 'sample_size',
          description: 'The number of sample rows for auto detection of parameters.',
          type: 'BIGINT',
          defaultValue: '20480',
        },
        {
          key: '25',
          name: 'skip',
          description: 'The number of lines at the top of the file to skip.',
          type: 'BIGINT',
          defaultValue: '0',
        },
        {
          key: '26',
          name: 'timestampformat',
          description: 'Specifies the date format to use when parsing timestamps.',
          type: 'VARCHAR',
          defaultValue: '(empty)',
        },
        {
          key: '27',
          name: 'types or dtypes',
          description: 'The column types as either a list (by position) or a struct (by name).',
          type: 'VARCHAR[] or STRUCT',
          defaultValue: '(empty)',
        },
        {
          key: '28',
          name: 'union_by_name',
          description: 'Whether the columns of multiple schemas should be unified by name rather than by position.',
          type: 'BOOL',
          defaultValue: 'false',
        },
    ],
}

const excelConf = {
    title : "Excel Configuration",
    columns: defaultColumns,
    data: [
        {
          key: '1',
          name: 'layer',
          description: 'Option to skip type detection for CSV parsing and assume all columns to be of type VARCHAR.',
          type: 'VARCHAR',
          defaultValue: '""',
        },
        {
          key: '2',
          name: 'open_option',
          description: 'Option to allow the conversion of quoted values to NULL values',
          type: 'VARCHAR[]',
          defaultValue: '[]',
        },
      ]
}

const jsonConf = {
    title: "JSON Configuration",
    columns: defaultColumns,
    data: [
        {
          key: "1",
          name: "auto_detect",
          description: "Whether to auto-detect the names of the keys and data types of the values automatically",
          type: "BOOL",
          defaultValue: 'false'
        },
        {
          key: "2",
          name: "columns",
          description: "A struct that specifies the key names and value types contained within the JSON file (e.g., {key1: 'INTEGER', key2: 'VARCHAR'}). If auto_detect is enabled these will be inferred",
          type: "STRUCT",
          defaultValue: "(empty)"
        },
        {
          key: "3",
          name: "compression",
          description: "The compression type for the file. By default, this will be detected automatically from the file extension (e.g., t.json.gz will use gzip, t.json will use none). Options are 'none', 'gzip', 'zstd', and 'auto'.",
          type: "VARCHAR",
          defaultValue: "'auto'"
        },
        {
          key: "4",
          name: "convert_strings_to_integers",
          description: "Whether strings representing integer values should be converted to a numerical type",
          type: "BOOL",
          defaultValue: 'false'
        },
        {
          key: "5",
          name: "dateformat",
          description: "Specifies the date format to use when parsing dates. See Date Format example below",
          type: "VARCHAR",
          defaultValue: "'iso'"
        },
        {
          key: "6",
          name: "filename",
          description: "Whether or not an extra filename column should be included in the result",
          type: "BOOL",
          defaultValue: 'false'
        },
        {
          key: "7",
          name: "format",
          description: "Can be one of ['auto', 'unstructured', 'newline_delimited', 'array']",
          type: "VARCHAR",
          defaultValue: "'array'"
        },
        {
          key: "8",
          name: "hive_partitioning",
          description: "Whether or not to interpret the path as a Hive partitioned path",
          type: "BOOL",
          defaultValue: 'false'
        },
        {
          key: "9",
          name: "ignore_errors",
          description: "Whether to ignore parse errors (only possible when format is 'newline_delimited')",
          type: "BOOL",
          defaultValue: 'false'
        },
        {
          key: "10",
          name: "maximum_depth",
          description: "Maximum nesting depth to which the automatic schema detection detects types. Set to -1 to fully detect nested JSON types",
          type: "BIGINT",
          defaultValue: -1
        },
        {
          key: "11",
          name: "maximum_object_size",
          description: "The maximum size of a JSON object (in bytes)",
          type: "UINTEGER",
          defaultValue: 16777216
        },
        {
          key: "12",
          name: "records",
          description: "Can be one of ['auto', 'true', 'false']",
          type: "VARCHAR",
          defaultValue: "'records'"
        },
        {
          key: "13",
          name: "sample_size",
          description: "Option to define the number of sample objects for automatic JSON type detection. Set to -1 to scan the entire input file",
          type: "UBIGINT",
          defaultValue: 20480
        },
        {
          key: "14",
          name: "timestampformat",
          description: "Specifies the date format to use when parsing timestamps. See Date Format example below",
          type: "VARCHAR",
          defaultValue: "'iso'"
        },
        {
          key: "15",
          name: "union_by_name",
          description: "Whether the schemas of multiple JSON files should be unified",
          type: "BOOL",
          defaultValue: 'false'
        }
      ]
      

}

const parquetConf = {
    title: "Parquet Configuration",
    columns: defaultColumns,
    data: [
        {
          key: "1",
          name: "binary_as_string",
          description: "Parquet files generated by legacy writers do not correctly set the UTF8 flag for strings, causing string columns to be loaded as BLOB instead. Set this to true to load binary columns as strings.",
          type: "BOOL",
          defaultValue: 'false'
        },
        {
          key: "2",
          name: "encryption_config",
          description: "Configuration for Parquet encryption.",
          type: "STRUCT",
          defaultValue: "-"
        },
        {
          key: "3",
          name: "filename",
          description: "Whether or not an extra filename column should be included in the result.",
          type: "BOOL",
          defaultValue: 'false'
        },
        {
          key: "4",
          name: "file_row_number",
          description: "Whether or not to include the file_row_number column.",
          type: "BOOL",
          defaultValue: 'false'
        },
        {
          key: "5",
          name: "hive_partitioning",
          description: "Whether or not to interpret the path as a Hive partitioned path.",
          type: "BOOL",
          defaultValue: 'false'
        },
        {
          key: "6",
          name: "union_by_name",
          description: "Whether the columns of multiple schemas should be unified by name, rather than by position.",
          type: "BOOL",
          defaultValue: 'false'
        }
      ]
      
}

const tableConfMapping = {
    flatfile : {
        "Flatfile csv": csvConf,
        "Flatfile excel": excelConf,
        "Flatfile json": jsonConf,
        "Flatfile parquet": parquetConf,
    }
}

const exampleConfMapping = {
    "Flatfile csv": `"config" : {
    "auto_detect" : true,
    "new_line" : "\n",
    "parallel" : true
}
`,
    "Flatfile excel": `"config" : {
    "layer" : ["Sheet1","Sheet2","Sheet3"],
    "open_options" : ["HEADERS=FORCE", "FIELD_TYPES=AUTO"]
}    
`,
    "Flatfile json": `"config" : {
    "auto_detect" : true,
    "ignore_errors" : true,
    "convert_strings_to_integers" : false,
    "columns": {
        "column_name" : "DATA_TYPE",
        "id": "INTEGER", 
        "name": "VARCHAR"
    }
}
`,
    "Flatfile parquet": `"config" : {
    "binary_as_string" : true,
    "file_row_number" : true,
}    
`,
}

export function getTableConfData(type, name) {
   const conf = tableConfMapping[type]?.[name];
   return conf || { title: "", columns: [], data: []}
}

export function getExampleConf(name) {
    return exampleConfMapping[name] || ``
}