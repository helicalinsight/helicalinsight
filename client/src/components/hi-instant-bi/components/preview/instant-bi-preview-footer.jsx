// import { CaretLeftOutlined, CaretRightOutlined, DeleteOutlined, MenuOutlined } from '@ant-design/icons';
import { Dropdown, Menu, Radio, Space, Tabs, Tooltip } from 'antd';
// import { act, useRef } from 'react';
import { useDispatch } from 'react-redux';
import { removeIBPreview, updateIBActivePreview } from '../../../../redux/actions/instant-bi.actions';

const InstantBIPreviewFooter = (props) => {
    const { previews = [], reportId, activePreview: { id: activePreviewId = '' } = {} } = props || {}
    const dispatch = useDispatch();

    // const scrollArrows = useRef(null)

    const onChange = (e) => {
        dispatch(updateIBActivePreview({ previewID: e, reportId }))
    };

    // const scrollBy = (value) => {
    //     let elem = document.querySelector(`#spaceElem-tab-list`)
    //     elem.scrollBy({
    //         top: 0,
    //         left: value,
    //         behavior: 'smooth'
    //     })
    // }

    const onRemove = (id) => {
        dispatch(removeIBPreview({ previewID: id, reportId }))
    }

    // const menuList = [
    //     { name: 'delete', label: 'Delete', onClick: onRemove, icon: <DeleteOutlined /> }
    // ]

    // const menu = (item) => {
    //     return <Menu.Item key={item.name} icon={item.icon} onClick={() => item.onClick(item.id)} >
    //         <Tooltip title={item.label}>
    //             <span >{item.label}</span>
    //         </Tooltip>
    //     </Menu.Item>
    // }

    // const getMenu = ({ id, items = null, menuList }) => {
    //     if (items) {
    //         return <Menu>
    //             {items.map((item) => {
    //                 return (
    //                     <Dropdown trigger={['contextMenu']} overlay={() => getMenu({ id: item.id, menuList })}>
    //                         <Menu.Item>{item.display}</Menu.Item>
    //                     </Dropdown>
    //                 )
    //             })}
    //         </Menu>
    //     }
    //     return (
    //         <Menu>
    //             {menuList.map(item => menu({ ...item, id }))}
    //         </Menu>
    //     )
    // }

    const onEdit = (targetKey, action) => {
        if (action === "remove") {
            onRemove(targetKey)
        }
    };

      const getTabItems = (data) => {
        if (!data.length) return [];
        return data.map((item, index) => {
          const { id = "", vf_title = "" } = item;
          const labelText = `${vf_title}.${index + 1}`;
          return {
            id,
            label: (
              <Tooltip title={labelText}>
                <span>
                  {labelText.length > 20
                    ? `${labelText.substring(0, 20)}...`
                    : labelText}
                </span>
              </Tooltip>
            ),
            key: id,
            children: null,
          };
        });
      };

    if (!previews.length) return null;

    return (
        <div className='instant-bi-footer-container'>
            <Tabs
                size='small'
                hideAdd
                onChange={onChange}
                activeKey={activePreviewId}
                type={previews.length > 1 ? "editable-card" : "card"}
                onEdit={onEdit}
                items={getTabItems(previews)}
            />
            {/* <div className='footer-menu-icon'>
                <Dropdown
                    trigger={["hover"]}
                    overlay={() => getMenu({ menuList, items: previews })}
                >

                    <MenuOutlined />
                </Dropdown>
            </div> */}

            {/* <Space
                align="center"
                className="tab-list"
                id="spaceElem-tab-list"
                onWheel={(e) => {
                    scrollBy(e.deltaY * 2.2)
                }}
            >
                <Radio.Group
                    value={activePreviewId}
                    onChange={onChange}
                >
                    {previews.map(({ display: label, id: value }) => {
                        return (
                            <Dropdown
                                overlay={() => getMenu({ id: value, menuList })}
                                trigger={["contextMenu"]}
                            >
                                <span><Radio.Button value={value} key={value}>{label}</Radio.Button></span>
                            </Dropdown>
                        )
                    })}
                </Radio.Group>
                {previews?.length > 7 ? <div className="scroll-items" ref={scrollArrows} >
                    <span onClick={() => scrollBy(-150)} ><CaretLeftOutlined size={16} /></span>
                    <span onClick={() => scrollBy(150)}><CaretRightOutlined /></span>
                </div> : null}
            </Space> */}
        </div>
    )
}

export default InstantBIPreviewFooter