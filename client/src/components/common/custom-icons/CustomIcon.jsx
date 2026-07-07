import Icon, { ApiOutlined, FileOutlined } from "@ant-design/icons";
import {
  Athena,
  Access,
  AmazonRedshift,
  AmazonDynamodb,
  ApacheDrill,
  Couchbase,
  Cassandra,
  Cosmosdb,
  Derby,
  DataBricks,
  Dremio,
  Dbase,
  Druid,
  ElasticSearch,
  Firebird,
  Greenplum,
  Googlecloud,
  Googlebigquery,
  HelicalMongodb,
  H2,
  Hive,
  Hbase,
  Hsql,
  IbmDB2,
  IBMNetezza,
  Impala,
  Informix,
  Influxdb,
  Ingres,
  Mariadb,
  MicrosoftSqlserver,
  MSAzure,
  MySQL,
  Marklogic,
  Memcached,
  Neo4j,
  Nuodb,
  SparkSQL,
  Sybase,
  Vertica,
  Splunk,
  Oracle,
  Postgresql,
  Presto,
  Clickhouse,
  SqLite,
  Teradata,
  Avro,
  Csv,
  Csvh,
  Excel,
  Json,
  Parquet,
  Pcap,
  Psv,
  Redis,
  Tsv,
  Sequencefile,
  Solr,
  SapHana,
  Default,
  ToggleSidebar,
  Square,
  Cube,
  Hierarchy,
  Dimension,
  Measure,
  Level,
  Snowflake,
  Trino,
  DuckDB,
  Yugabyte,
  CelerDb,
  CockroachDb,
  Aws,
  GoogleSpreadsheet,
  FlatfileCloudfare,
  HcrImageSvg
} from "./CustomSvg.jsx";

const AccessIcon = () => <Icon component={Access} />;
const AthenaIcon = () => <Icon component={Athena} />;
const AmazonRedshiftIcon = () => <Icon component={AmazonRedshift} />;
const AmazondynamodbIcon = () => <Icon component={AmazonDynamodb} />;
const ApacheDrillIcon = () => <Icon component={ApacheDrill} />;
const CouchbaseIcon = () => <Icon component={Couchbase} />;
const CosmosdbIcon = () => <Icon component={Cosmosdb} />;
const CassandraIcon = () => <Icon component={Cassandra} />;
const DbaseIcon = () => <Icon component={Dbase} />;
const DruidIcon = () => <Icon component={Druid} />;
const ApacheDerbyIcon = () => <Icon component={Derby} />;
const DremioIcon = () => <Icon component={Dremio} />;
const DataBricksIcon = () => <Icon component={DataBricks} />;
const ElasticSearchIcon = () => <Icon component={ElasticSearch} />;
const GreenplumIcon = () => <Icon component={Greenplum} />;
const GooglecloudIcon = () => <Icon component={Googlecloud} />;
const GooglebigqueryIcon = () => <Icon component={Googlebigquery} />;
const HelicalMongodbIcon = () => <Icon component={HelicalMongodb} />;
const HiveIcon = () => <Icon component={Hive} />;
const H2Icon = () => <Icon component={H2} />;
const HsqlIcon = () => <Icon component={Hsql} />;
const HbaseIcon = () => <Icon component={Hbase} />;
const IBMNetezzaIcon = () => <Icon component={IBMNetezza} />;
const IbmDB2Icon = () => <Icon component={IbmDB2} />;
const ImpalaIcon = () => <Icon component={Impala} />;
const InformixIcon = () => <Icon component={Informix} />;
const InfluxdbIcon = () => <Icon component={Influxdb} />;
const IngresIcon = () => <Icon component={Ingres} />;
const MariadbIcon = () => <Icon component={Mariadb} />;
const MicrosoftSqlserverIcon = () => <Icon component={MicrosoftSqlserver} />;
const MicrosoftAzureIcon = () => <Icon component={MSAzure} />;
const MySQLIcon = () => <Icon component={MySQL} />;
const MemcachedIcon = () => <Icon component={Memcached} />;
const MarklogicIcon = () => <Icon component={Marklogic} />;
const Neo4jIcon = () => <Icon component={Neo4j} />;
const NuodbIcon = () => <Icon component={Nuodb} />;
const OracleIcon = () => <Icon component={Oracle} />;
const PostgresqlIcon = () => <Icon component={Postgresql} />;
const PrestoIcon = () => <Icon component={Presto} />;
const ClickhouseIcon = () => <Icon component={Clickhouse} />;
const SqLiteIcon = () => <Icon component={SqLite} />;
const SapHanaIcon = () => <Icon component={SapHana} />;
const SybaseIcon = () => <Icon component={Sybase} />;
const SplunkIcon = () => <Icon component={Splunk} />;
const TeradataIcon = () => <Icon component={Teradata} />;
const AvroIcon = () => <Icon component={Avro} />;
const CsvIcon = () => <Icon component={Csv} />;
const CsvhIcon = () => <Icon component={Csvh} />;
const ExcelIcon = () => <Icon component={Excel} />;
const FirebirdIcon = () => <Icon component={Firebird} />;
const JsonIcon = () => <Icon component={Json} />;
const ParquetIcon = () => <Icon component={Parquet} />;
const PcapIcon = () => <Icon component={Pcap} />;
const PsvIcon = () => <Icon component={Psv} />;
const RedisIcon = () => <Icon component={Redis} />;
const SequencefileIcon = () => <Icon component={Sequencefile} />;
const SolrIcon = () => <Icon component={Solr} />;
const SparkSQLIcon = () => <Icon component={SparkSQL} />;
const TsvIcon = () => <Icon component={Tsv} />;
const VerticaIcon = () => <Icon component={Vertica} />;
const LtsvIcon = () => <Icon component={Tsv} />;
const DefaultIcon = () => <Icon component={Default} />;
const ToggleSidebarIcon = () => <Icon component={ToggleSidebar} />;
const SquareIcon = () => <Icon component={Square} />;
const SnowflakeIcon = () => <Icon component={Snowflake} />;
const TrinoIcon = () => <Icon component={Trino} />;
const DuckDBIcon = () => <Icon component={DuckDB} />
const YugabyteIcon = () => <Icon component={Yugabyte} />
const CelerDbIcon = () => <Icon component={CelerDb} />
const CockroachDbIcon = () => <Icon component={CockroachDb} />
const AwsDbIcon = () => <Icon component={Aws} />
const GoogleSpreadsheetDbIcon = () => <Icon component={GoogleSpreadsheet} />

const CubeIcon = ({ }) => (
  <Icon className="cube-icon" style={{ height: "18px", width: "16px" }} component={Cube} />
); //<Icon component={Cube} />; <Cube />; <Icon component={() => <div>bhvh</div>} />
const HierarchyIcon = () => (
  <Icon
    className="hierarchy-icon"
    style={{ height: "18px", width: "16px" }}
    component={Hierarchy}
  />
);
const LevelIcon = () => (
  <Icon className="level-icon" style={{ height: "18px", width: "16px" }} component={Level} />
);
const DimensionIcon = () => (
  <Icon
    className="dimension-icon"
    style={{ height: "18px", width: "16px" }}
    component={Dimension}
  />
);
const MeasureIcon = () => (
  <Icon className="measure-icon" style={{ height: "18px", width: "16px" }} component={Measure} />
);
const TaskbarIcon = () => {
  return <ToggleSidebarIcon />;
};

const HcrImageIcon = () => <Icon component={HcrImageSvg} />;

const CustomIcon = ({ name }) => {
  switch (name) {
    case "HcrImage":
      return <HcrImageIcon />;
    case "Level":
      return <LevelIcon />;
    case "Measure":
      return <MeasureIcon />;
    case "Hierarchy":
      return <HierarchyIcon />;
    case "Dimension":
      return <DimensionIcon />;
    case "Square":
      return <SquareIcon />;
    case "Trino":
      return <TrinoIcon />;
    case "Access":
      return <AccessIcon />;
    case "Athena":
      return <AthenaIcon />;
    case "Amazon Redshift":
      return <AmazonRedshiftIcon />;
    case "Amazon Dynamodb":
      return <AmazondynamodbIcon />;
    case "Apache Drill":
      return <ApacheDrillIcon />;
    case "Couchbase":
      return <CouchbaseIcon />;
    case "Cosmosdb":
      return <CosmosdbIcon />;
    case "Cassandra":
      return <CassandraIcon />;
    case "Cube":
      return <CubeIcon />;
    case "Dbase":
      return <DbaseIcon />;
    case "Druid":
      return <DruidIcon />;
    case "Derby":
      return <ApacheDerbyIcon />;
    case "Dremio":
      return <DremioIcon />;
    case "Databricks":
      return <DataBricksIcon />;
    case "Elasticsearch":
      return <ElasticSearchIcon />;
    case "Firebirdsql":
      return <FirebirdIcon />;
    case "Greenplum":
      return <GreenplumIcon />;
    case "Google Cloud":
      return <GooglecloudIcon />;
    case "Google Bigquery":
      return <GooglebigqueryIcon />;
    case "Helical Mongodb":
      return <HelicalMongodbIcon />;
    case "Hive":
      return <HiveIcon />;
    case "Hbase":
      return <HbaseIcon />;
    case "H2":
      return <H2Icon />;
    case "Hsqldb":
      return <HsqlIcon />;
    case "IBMNetezza":
      return <IBMNetezzaIcon />;
    case "IBM Db2":
      return <IbmDB2Icon />;
    case "Impala":
      return <ImpalaIcon />;
    case "Informix":
      return <InformixIcon />;
    case "Influxdb":
      return <InfluxdbIcon />;
    case "Ingres":
      return <IngresIcon />;
    case "Mariadb":
      return <MariadbIcon />;
    case "Microsoft Sqlserver":
      return <MicrosoftSqlserverIcon />;
    case "Microsoft Sqlserver(sourceforge)":
      return <MicrosoftSqlserverIcon />;
    case "Microsoft Azure":
      return <MicrosoftAzureIcon />;
    case "Mongodb":
      return <HelicalMongodbIcon />;
    case "Mysql":
      return <MySQLIcon />;
    case "Mysql Cj":
      return <MySQLIcon />;
    case "Memcached":
      return <MemcachedIcon />;
    case "Marklogic":
      return <MarklogicIcon />;
    case "Neo4j":
      return <Neo4jIcon />;
    case "Nuodb":
      return <NuodbIcon />;
    case "Oracle":
      return <OracleIcon />;
    case "Postgresql":
      return <PostgresqlIcon />;
    case "Presto":
      return <PrestoIcon />;
    case "Clickhouse":
      return <ClickhouseIcon />;
    case "Sqlite":
      return <SqLiteIcon />;
    case "Sap Db":
      return <SapHanaIcon />;
    case "Sybase":
      return <SybaseIcon />;
    case "Splunk":
      return <SplunkIcon />;
    case "Teradata":
      return <TeradataIcon />;
    case "Avro":
      return <AvroIcon />;
    case "Csv":
      return <CsvIcon />;
    case "Csvh":
      return <CsvhIcon />;
    case "Excel":
      return <ExcelIcon />;
    case "Json":
      return <JsonIcon />;
    case "Parquet":
      return <ParquetIcon />;
    case "Pcap":
      return <PcapIcon />;
    case "Pcapng":
      return <PcapIcon />;
    case "Psv":
      return <PsvIcon />;
    case "Redis":
      return <RedisIcon />;
    case "Sequencefile":
      return <SequencefileIcon />;
    case "Solr":
      return <SolrIcon />;
    case "SparkSQL":
      return <SparkSQLIcon />;
    case "Tsv":
      return <TsvIcon />;
    case "Vertica":
      return <VerticaIcon />;
    case "Ltsv":
      return <LtsvIcon />;
    case "Snowflake":
      return <SnowflakeIcon />;
    case "Duckdb":
      return <DuckDBIcon />;
    case "Yugabyte":
      return <YugabyteIcon />;
    case "Celerdata":
      return <CelerDbIcon />;
    case "Cockroach":
      return <CockroachDbIcon />;
    case "API":
      return <ApiOutlined style={{ fontSize: "30px" }} />;
    case "Flatfile":
      return <FileOutlined style={{ fontSize: "30px" }} />;
    case "Flatfile csv":
      return <CsvIcon />;
    case "Flatfile excel":
      return <ExcelIcon />;
    case "Flatfile json":
      return <JsonIcon />;
    case "Flatfile tsv":
      return <TsvIcon />;
    case "Flatfile aws":
      return <AwsDbIcon />;
    case "Flatfile azure blobstorage":
      return <MicrosoftAzureIcon />;
    case "Flatfile cloudfare r2":
      return <FlatfileCloudfare />;
    case "Flatfile GCS":
      return <GooglecloudIcon />;
    case "Flatfile parquet":
      return <ParquetIcon />;
    case "Flatfile Google spreadsheet":
      return <GoogleSpreadsheetDbIcon />;
    default:
      return <DefaultIcon />;
  }
};

export { CustomIcon, TaskbarIcon };
