import { FileImageOutlined, FileUnknownOutlined } from "@ant-design/icons";
import { Image } from "antd";
import { hcrImageFallback } from "../../hcr-constants";
import { useEffect, useState } from "react";
import requests from "../../../../base/requests";
import { useDispatch } from "react-redux";

const ImageNode = (props) => {
  const { data, isElementRender } = props;
  const {
    label,
    fill = "#ffffff",
    fontFill = "#000000",
    verticalAlign = "middle",
    horizontalAlign = "center",
    mode = "Transparent",
    width = 100,
    height = 100,
    borders = {},
    padding = {},
    image = "",
    scaleImage = "Clip",
    fillType = "Solid",
    imagePath = "",
    type = "internal",
    link = "",
    dir = "",
    file = "",
    customExpressionToggle = false
  } = data;

  const defaultBorder = `0px solid #000000`;
  const defaultPadding = 0;

  const padTop = padding.Top || defaultPadding;
  const padBottom = padding.Bottom || defaultPadding;
  const padLeft = padding.Left || defaultPadding;
  const padRight = padding.Right || defaultPadding;

  const innerWidth = Math.max(0, width - padLeft - padRight);
  const innerHeight = Math.max(0, height - padTop - padBottom);
  const dispatch = useDispatch();
  const { hcrImageServiceRequest } = requests.cannedReport(dispatch);
  const [imageState, setImageState] = useState({
    data: null,
    error: false,
  });
  const alignItems = {
    top: "flex-start",
    middle: "center",
    bottom: "flex-end",
  }[verticalAlign];

  const justifyContent = {
    left: "flex-start",
    center: "center",
    right: "flex-end",
  }[horizontalAlign];

  const isBase64 = imagePath?.startsWith("data:image/");
  const isExternal =
    imagePath?.startsWith("http://") || imagePath?.startsWith("https://");
  let extractedDir = "";
  let extractedFile = "";
  if (imagePath && !isBase64 && !isExternal) {
    const parts = imagePath.split("/");
    extractedFile = parts.pop() || "";
    extractedDir = parts.join("/");
  }
  let imageSrc = isBase64
    ? imagePath
    : isExternal
      ? imagePath
      : extractedDir && extractedFile
        ? `${window.baseURL}/getExternalResource?path=${extractedDir}/${extractedFile}`
        : null;

  if (customExpressionToggle) {
    imageSrc = null; // Custom expression will determine the image source, so we set it to null here
  }

  useEffect(() => {
    if (!imagePath || isExternal) {
      setImageState({
        data: isExternal ? { status: 1 } : null,
        error: !isExternal,
      });
      return;
    }
    if (isBase64) {
      setImageState({
        data: { status: 1, response: { data: {} } },
        error: false,
      });
      return;
    }
    const isValidPath = extractedDir && extractedFile;
    setImageState((prev) => ({ ...prev, error: !isValidPath }));
    if (!isValidPath) {
      setImageState({ data: null, error: true });
      return;
    }
    //imageservice api-call
    hcrImageServiceRequest(
      { dir: extractedDir, file: extractedFile },
      "util/io/imageService",
      (res) => {
        const hasError =
          res?.response?.message ||
          res?.status === 0;
        setImageState({
          data: hasError ? null : res,
          error: hasError,
        });
      },
      () => {
        setImageState({ data: null, error: true });
      },
    );
  }, [imagePath, isBase64, isExternal, extractedDir, extractedFile]);

  const getImageStyle = () => {
    switch (scaleImage) {
      case "Clip":
        let listImagestyles = {
          backgroundRepeat: "no-repeat",
          backgroundPosition: getImagePosition(horizontalAlign, verticalAlign),
          backgroundSize: "auto",
          width: innerWidth,
          height: innerHeight,
        };
        if (imageSrc && !imageState.error) {
          listImagestyles.backgroundImage = `url(${imageSrc})`;
        }
        return listImagestyles;
      case "FillFrame":
        return {
          objectFit: "fill",
          width: innerWidth,
          height: innerHeight,
        };
      case "RetainShape":
        return {
          objectFit: "contain",
          width: innerWidth,
          height: innerHeight,
        };
      case "RealHeight":
        return {
          height: innerHeight,
        };
      case "RealSize":
        return {
          width: innerWidth,
          height: innerHeight,
        };
      default:
        return {};
    }
  };

  const getImagePosition = (horizontal, vertical) => {
    const h = {
      left: "left",
      center: "center",
      right: "right",
    }[horizontal];

    const v = {
      top: "top",
      middle: "center",
      bottom: "bottom",
    }[vertical];

    return `${h} ${v}`;
  };

  const containerStyle = {
    width,
    height,
    display: "flex",
    backgroundColor:
      mode.toLowerCase() === "transparent"
        ? "transparent"
        : fillType === "Solid"
          ? fill
          : "transparent",
    alignItems,
    justifyContent: isElementRender ? "none" : justifyContent,
    color: fontFill,
    borderTop: borders.Top
      ? `${borders.Top.stroke}px ${borders.Top.style?.toLowerCase()} ${borders.Top.color
      }`
      : defaultBorder,
    borderBottom: borders.Bottom
      ? `${borders.Bottom.stroke}px ${borders.Bottom.style?.toLowerCase()} ${borders.Bottom.color
      }`
      : defaultBorder,
    borderLeft: borders.Left
      ? `${borders.Left.stroke}px ${borders.Left.style?.toLowerCase()} ${borders.Left.color
      }`
      : defaultBorder,
    borderRight: borders.Right
      ? `${borders.Right.stroke}px ${borders.Right.style?.toLowerCase()} ${borders.Right.color
      }`
      : defaultBorder,
  };

  const paddingStyle = {
    marginTop: padTop,
    marginBottom: padBottom,
    marginLeft: padLeft,
    marginRight: padRight,
    // width: innerWidth,
    // height: innerHeight,
    // display: 'flex',
    // alignItems,
    // justifyContent,
    // overflow: scaleImage === 'Clip' ? 'hidden' : 'visible',
  };

  //iconn siozee
  const getIconSize = () => {
    const availableWidth = innerWidth;
    const availableHeight = innerHeight;
    const minSize = 16;
    const maxSize = Math.min(availableWidth, availableHeight) * 0.8;
    const size = Math.min(availableWidth, availableHeight) * 0.6;
    return Math.max(minSize, Math.min(size, maxSize));
  };
  const iconSize = getIconSize();
  const hasImage = Boolean(imagePath && imagePath.trim()) && !customExpressionToggle;

  return (
    <div className="rectangle-node-container" style={containerStyle}>
      <div style={paddingStyle}>
        {isElementRender ? (
          <div className="element-wrapper">
            <FileImageOutlined className="ele-icn" />
            <span>{label}</span>
          </div>
        ) : !hasImage ? (
          <div
            className="image-placeholder"
            style={{
              width: "100%",
              height: "100%",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            {/* <img src="/path/to/your-icon.svg" width={24} height={24} /> */}
            <FileImageOutlined
              style={{
                color: "black",
                opacity: 0.4,
                fontSize: `${iconSize}px`,
                width: iconSize,
                height: iconSize,
              }}
            />
          </div>
        ) : imageState.error ? (
          <FileUnknownOutlined
            style={{ color: "black", opacity: 0.4, fontSize: "24px" }}
          />
        ) : scaleImage === "Clip" ? (
          <div style={{ ...getImageStyle(), overflow: "hidden" }} />
        ) : (
          <Image
            preview={false}
            src={imageSrc}
            fallback={hcrImageFallback}
            style={getImageStyle()}
          />
        )}
      </div>
    </div>
  );
};

export default ImageNode;