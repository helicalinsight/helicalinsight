import { Fragment } from "react";
import { DownOutlined, EditFilled } from "@ant-design/icons";
import { Dropdown, List, Tooltip, Checkbox } from "antd";
import { useDispatch, useSelector } from "react-redux";
import { appActions } from "../../../redux/actions";
import { leftMenuDrop, taskbarItems } from "../helperMethods";
import { TaskbarIcon } from "../../common/custom-icons/CustomIcon";
import TutorialInfo from "../../common/hi-tutorial";
import ShortCutText from "../../common/hi-shortcuts/hi-shortcuts";
import { getTooltipTitle } from "../../hi-reports/hr-taskbar-items";

const { toggleSidebar } = appActions;

const LayoutOptions = ({ options, defaultValue, callBack }) => {
  return (
    <Checkbox.Group
      onChange={callBack}
      options={options}
      defaultValue={defaultValue}
    />
  );
};

const Taskbar = ({ activeRoute, taskbar, hideToggleSidebar = false }) => {
  const tutorialData = useSelector((state) => state.app.tutorialData);
  let leftMenu = taskbar ? taskbar : taskbarItems(activeRoute);
  const dispatch = useDispatch();
  if (
    (activeRoute.match(/admin/) ||
      activeRoute.match(/datasource/) ||
      activeRoute.match(/report-ce/) ||
      activeRoute.match(/report-viewer/) ||
      activeRoute.match(/hi/) ||
      activeRoute.match(/welcome/) ||
      activeRoute.match(/metadata/) ||
      activeRoute.match(/hcr/) ||
      activeRoute.match(/cube/) ||
      activeRoute.match(/agent/)) && !hideToggleSidebar
  ) {
    leftMenu = [
      ...leftMenu,
      {
        tooltip: "Toggle Sidebar",
        customClassName: "hi-navbar-toggle-sidebar-icon",
        icon: <TaskbarIcon />,
        callBack: () => {
          dispatch(toggleSidebar());
        },
      },
    ];
  }
  return (
    <Fragment>
      {leftMenu.length ? (
        <List
          className="hi-navbar-right"
          rowKey={(ele, i) => {
            return i;
          }}
          style={{ overflowX: 'visible', overflowY: 'visible' }}
          dataSource={leftMenu}
          renderItem={(ele) => {
            let className = ele.dropdown
              ? "hi-navbar-right-item hi-navbar-right-item-dropdown"
              : "hi-navbar-right-item";
            if (ele.itemClz) {
              className = `${className} ${ele.itemClz}`
            }
            return (
              <List.Item className={className}>

                <div>
                  {ele.dropdown || ele.dropdownCheckList ? (
                    <Dropdown
                      arrow="true"
                      id={ele.id && ele.id}
                      ref={ele.ref && ele.ref}
                      trigger="click"
                      overlay={
                        ele.dropdown ? (
                          leftMenuDrop(ele.dropdown)
                        ) : (
                          <LayoutOptions
                            callBack={ele.dropdownCheckList.callBack}
                            options={ele.dropdownCheckList.checkListOptions}
                            defaultValue={
                              ele.dropdownCheckList.checkListOptions
                            }
                          />
                        )
                      }
                    >
                      {/* <Tooltip
                        placement={
                          ele.dropdown || ele.dropdownCheckList ? "top" : "bottom"
                        }
                        title={getTooltipTitle(ele.tooltip, ele.shortCut)}
                      > */}
                      <span className="taskbar-item">
                        {ele.tutorialKey && tutorialData ? (
                          <TutorialInfo elementKey={ele.tutorialKey}>
                            <ShortCutText navItem={true} text={ele.scText} scLocation={ele.scLocation} id={ele.id && ele.id} dropdownItem={ele.dropdown}>
                              <Tooltip
                                placement={
                                  ele.dropdown || ele.dropdownCheckList ? "top" : "bottom"
                                }
                                title={getTooltipTitle(ele.tooltip, ele.shortCut)}
                              >
                                <span id={ele.id && ele.id} data-testid={ele.dataTestId ? ele.dataTestId : false} onClick={ele.callBack} ref={ele.ref && ele.ref}>{ele.icon}</span>
                              </Tooltip>
                            </ShortCutText>
                          </TutorialInfo>
                        ) : (
                          <ShortCutText dropdownItem={ele.dropdown} navItem={true} text={ele.scText} scLocation={ele.scLocation} id={ele.id && ele.id} >
                            <Tooltip
                              placement={
                                ele.dropdown || ele.dropdownCheckList ? "top" : "bottom"
                              }
                              title={getTooltipTitle(ele.tooltip, ele.shortCut)}
                            >
                              <span id={ele.id && ele.id} data-testid={ele.dataTestId ? ele.dataTestId : false} onClick={ele.callBack} ref={ele.ref && ele.ref}>{ele.icon}</span>
                            </Tooltip>
                          </ShortCutText>
                        )}
                        <DownOutlined
                          style={{ fontSize: "10px", marginTop: "3px" }}
                        />
                      </span>
                      {/* </Tooltip> */}
                    </Dropdown>
                  ) : (
                    // <Tooltip
                    //   placement={
                    //     ele.dropdown || ele.dropdownCheckList ? "top" : "bottom"
                    //   }
                    //   title={getTooltipTitle(ele.tooltip, ele.shortCut)}
                    // >
                    <span
                      className={ele.customClassName || "taskbar-item"}
                      data-testid={ele.customClassName || ""}
                      style={{ opacity: ele.disabled ? "0.5" : 1 }}
                    >
                      {ele.tutorialKey && tutorialData ? (
                        <TutorialInfo elementKey={ele.tutorialKey}>
                          <ShortCutText dropdownItem={ele.dropdown} navItem={true} text={ele.scText} scLocation={ele.scLocation} id={ele.id && ele.id} >
                            <Tooltip
                              placement={
                                ele.dropdown || ele.dropdownCheckList ? "top" : "bottom"
                              }
                              title={getTooltipTitle(ele.tooltip, ele.shortCut)}
                            >
                              <span data-testid={ele.dataTestId ? ele.dataTestId : false} onClick={ele.callBack} ref={ele.ref && ele.ref}>{ele.icon}</span>
                            </Tooltip>
                          </ShortCutText>
                        </TutorialInfo>
                      ) : (
                        <ShortCutText dropdownItem={ele.dropdown} navItem={true} text={ele.scText} scLocation={ele.scLocation} id={ele.id && ele.id} >
                          <Tooltip
                            overlayClassName="task-item-no-taskbar"
                            // style={{ width: '140%' }}
                            placement={
                              ele.dropdown || ele.dropdownCheckList ? "top" : "bottom"
                            }
                            title={getTooltipTitle(ele.tooltip, ele.shortCut)}
                          >
                            <span data-testid={ele.dataTestId ? ele.dataTestId : false} onClick={ele.callBack} ref={ele.ref && ele.ref}>{ele.icon}</span>
                          </Tooltip>
                        </ShortCutText>
                      )}
                    </span>
                    // </Tooltip>
                  )}
                </div>
              </List.Item>
            );
          }}
        />
      ) : null}
    </Fragment >
  );
};

export default Taskbar;
