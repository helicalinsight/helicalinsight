import { SearchOutlined } from '@ant-design/icons';
import { Button, Input, Space } from 'antd';
import Highlighter from 'react-highlight-words';

export const capitalize = (text) => text[0].toUpperCase() + text.slice(1);

  export const handleOwnershipFormdata = ({recyclebinItem, selectedUser}) => {
    return {
      "type": recyclebinItem?.type,
      "resources": [recyclebinItem.data?.id],
      "newOwnerId": selectedUser[0]
    };
  }

export const handleFetchDetailsDataSource = ({recyclebinItemRows, res}) => {
  const newArr = [];
  recyclebinItemRows.forEach(obj => {
      const data = {
          resourceName: obj.name,
          recycleBinId: obj.recycleBinId,
          key: obj.recycleBinId,
          associatedDetails: {
              resourcesData: res[obj.recycleBinId]["data"]?.resources || [],
              dataSourcesData: res[obj.recycleBinId]["data"]?.dataSources || [],
              usersData: res[obj.recycleBinId]["data"]?.users || [],
          }
      }
      newArr.push(data);
  })
  return newArr;
}

export const handleSearch = (selectedKeys, confirm, dataIndex, setSearchText, setSearchedColumn) => {
    confirm();
    setSearchText(selectedKeys[0]);
    setSearchedColumn(dataIndex);
};
export const handleReset = (clearFilters, setSearchText, confirm) => {
    clearFilters();
    setSearchText('');
    confirm();
};

export const handleSlnoOrder = (rec, i) => {
  let dupRec = {...rec};
  dupRec.slno = i + 1;
  return dupRec;
}

export const getRecycleBinColumnSearchProps = ({dataIndex, searchText, searchedColumn, searchInput, setSearchText, setSearchedColumn}) => ({
    // filterDropdownOpen: false,
    filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters, close=()=> {} }) => (
      <div
        style={{
          padding: 8,
        }}
        onKeyDown={(e) => e.stopPropagation()}
      >
        <Input
          ref={searchInput}
          placeholder={`Search ${dataIndex === "data" ? "Resource" : capitalize(dataIndex)}`}
          value={selectedKeys[0]}
          onChange={(e) => setSelectedKeys(e.target.value ? [e.target.value] : [])}
          onPressEnter={() => handleSearch(selectedKeys, confirm, dataIndex, setSearchText, setSearchedColumn)}
          style={{
            marginBottom: 8,
            display: 'block',
          }}
        />
        <Space>
          <Button
            type="primary"
            onClick={() => handleSearch(selectedKeys, confirm, dataIndex, setSearchText, setSearchedColumn)}
            icon={<SearchOutlined />}
            size="small"
            style={{
              width: 90,
            }}
          >
            Search
          </Button>
          <Button
            onClick={() => clearFilters && handleReset(clearFilters, setSearchText, confirm)}
            size="small"
            style={{
              width: 90,
            }}
          >
            Reset
          </Button>
        </Space>
      </div>
    ),
    filterIcon: (filtered) => (
      <SearchOutlined
        style={{
          color: filtered ? '#1677ff' : undefined,
        }}
      />
    ),
    onFilter: (value, record) => {
      if (dataIndex === "data") {
        return record[dataIndex].name.toString().toLowerCase().includes(value.toLowerCase());
      }
      return record[dataIndex].toString().toLowerCase().includes(value.toLowerCase());
    },
    onFilterDropdownOpenChange: (visible) => {
      if (visible) {
        setTimeout(() => searchInput.current?.select(), 100);
      }
    },
    render: (text) =>
      searchedColumn === dataIndex ? (
        <Highlighter
          highlightStyle={{
            backgroundColor: '#ffc069',
            padding: 0,
          }}
          searchWords={[searchText]}
          autoEscape
          textToHighlight={text ? text.toString() : ''}
        />
      ) : (
        text
      ),
  });