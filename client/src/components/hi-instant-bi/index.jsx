import { Layout } from "antd";
import { isEmpty } from "lodash";
import { useEffect, useState } from "react";
import { Responsive, WidthProvider } from "react-grid-layout";
import { useDispatch, useSelector } from "react-redux";
import { useWindowSize } from "../../customHooks/useWindowSize";
import { updateInstantBILayout } from "../../redux/actions/instant-bi.actions";
import HiMetadataArea from "../hi-sidebar/hr-hreportSidebar/hi-metadata-area";
import InstantBIChatScreen from "./components/chat-screen/chat-screen";
import InfoContainer from "./components/info-container/info-container";
import InstantBIPreview from "./components/preview/instant-bi-preview";
import "./index.scss";
import { createInsantBIGridItems } from "./utils/common-utils";

const ResponsiveGridLayout = WidthProvider(Responsive);

const styles = {
  border: '1px solid #ddd',
}

const HIInstantBIModule = (props) => {
  const { activeReport = {}, reportId, isMetadataPresent, connectMetadata = () => { } } = props || {};
  const [width, height] = useWindowSize();
  const [savedLayout, setSavedLayout] = useState(null);
  const [, offsetHeight] = useWindowSize();
  const dispatch = useDispatch();
  const { metadata = {}, botStatus, mode } = activeReport;
  const { layout } = useSelector((state) => state.instantBI);
  const { activeChatID, chats = [], previews = [], activePreviewID = null } = activeReport;
  const messageList = chats?.find((chat) => chat?.chatID === activeChatID)?.messageList || [];
  const { activeReportId = '' } = useSelector((state) => state.instantBI)

  const { metadataShelf, previewShelf, chatShelf } = layout || {};
  let [gridItems, setGridItems] = useState(createInsantBIGridItems({ metadataShelf, previewShelf, chatShelf, offsetHeight }));
  let isOpenMode = ['open'].includes(mode);
  const isEditMode = ['edit'].includes(mode);

  let metadataTooltip = "", metadataInfoPaneIcon = false
  if (!isEmpty(metadata)) {
    metadataInfoPaneIcon = true
    metadataTooltip = (<div style={{ fontSize: 12 }}>
      <div><span>Name:</span> <span>{metadata?.formData?.title ?? metadata?.metadataName}</span></div>
      <div><span>Location:</span> <span>{metadata?.formData?.path}</span></div>
    </div>)
  }


  const gridProps = {
    resizeHandles: ["se"],
    cols: { lg: 100, md: 100, sm: 100, xs: 100, xxs: 100 },
    breakpoints: { lg: 1200, md: 996, sm: 768, xs: 480, xxs: 0 },
    isDroppable: true,
    compactType: null,
    preventCollision: true,
    rowHeight: height > 680 ? 1 : 0.7,
    colWidth: 1,
    margin: [5, 10],
    containerPadding: [1, 1],
    measureBeforeMount: true,
    useCSSTransforms: false
  };

  let chatProps = {
    activeReportId,
    activeReport,
    metadata,
    isMetadataPresent,
    activeChatID,
    messageList,
    botStatus,
    reportId,
    previews,
    activePreviewID,
    connectMetadata,
    isOpenMode,
    isEditMode
  }

  const handleShelf = (key) => {
    dispatch(updateInstantBILayout({ key: key, value: false }))
  }

  const metadataGridItem = gridItems?.lg?.find((item) => item.i === "metadata-area");
  const parentContainerId = `hi-instant-bi-${reportId.slice(0, 6)}`

  useEffect(() => {
    setGridItems(() => {
      return createInsantBIGridItems({ metadataShelf, previewShelf, chatShelf, offsetHeight })
    });
  }, [metadataShelf, previewShelf, chatShelf, offsetHeight]);

  return (
    <Layout className={`hi-instant-bi hr-sidebar${isOpenMode ? " hi-instant-bi--open-mode" : ""}`}>
      <ResponsiveGridLayout
        onLayoutChange={(layout) => {
          if (!savedLayout) {
            setSavedLayout({ prev: undefined, current: layout });
          } else {
            setSavedLayout({ prev: savedLayout.current, current: layout });
          }
        }}
        layouts={gridItems}
        {...gridProps}
      >
        {metadataShelf && !isOpenMode &&
          <div style={styles} key="metadata-area">
            <InfoContainer
              pane="Metadata"
              onClose={() => handleShelf('metadataShelf')}
              icon={metadataInfoPaneIcon}
              iconTooltip={metadataTooltip}
            />
            {metadata ?
              <HiMetadataArea
                metadata={metadata}
                size={{ height: metadataGridItem.h }}
                module='instantBI'
                isInstantBI={true}
                metadataLoading={metadata?.loading}
                parentContainerId={parentContainerId}
              />
              : null}
          </div>
        }
        {previewShelf && !isOpenMode ?
          <div style={styles} key="preview-area">
            <InfoContainer pane="Previews" onClose={() => handleShelf('previewShelf')} />
            <InstantBIPreview {...chatProps} />
          </div> : null
        }
        {(chatShelf || isOpenMode) ?
          <div style={styles} key="chat-area"><InstantBIChatScreen {...chatProps} /></div>
          : null
        }
      </ResponsiveGridLayout>
    </Layout>
  );
};
export { HIInstantBIModule };

