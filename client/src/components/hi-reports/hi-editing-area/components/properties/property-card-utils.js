import React from 'react';
import Icon from '@ant-design/icons';
import {
  ArrowUpOutlined,
  ArrowDownOutlined,
  ArrowLeftOutlined,
  ArrowRightOutlined,
  UpSquareOutlined,
  DownSquareOutlined,
  LeftSquareOutlined,
  AreaChartOutlined,
  RightSquareOutlined,
  ExclamationOutlined,
  ExclamationCircleOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  StarOutlined,
  StarFilled,
  PieChartOutlined,
  BarChartOutlined,
  DotChartOutlined,
  LineChartOutlined,
  HeatMapOutlined,
  FallOutlined,
  RiseOutlined,
  StockOutlined,
  BoxPlotOutlined,
  FundOutlined,
  SlidersOutlined,
  StepBackwardOutlined,
  StepForwardOutlined,
  FastBackwardOutlined,
  FastForwardOutlined,
  ShrinkOutlined,
  ArrowsAltOutlined,
  DownOutlined,
  UpOutlined,
  LeftOutlined,
  RightOutlined,
  CaretUpOutlined,
  CaretDownOutlined,
  CaretLeftOutlined,
  CaretRightOutlined,
  DoubleRightOutlined,
  DoubleLeftOutlined,
  VerticalLeftOutlined,
  VerticalRightOutlined,
  VerticalAlignTopOutlined,
  VerticalAlignMiddleOutlined,
  VerticalAlignBottomOutlined,
  ForwardOutlined,
  BackwardOutlined,
  RollbackOutlined,
  EnterOutlined,
  RetweetOutlined,
  SwapOutlined,
  SwapLeftOutlined,
  SwapRightOutlined,
  PlayCircleOutlined,
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  CheckOutlined,
  CloseOutlined,
  AccountBookOutlined,
  AimOutlined,
  AlertOutlined,
  ApartmentOutlined,
  ApiOutlined,
  AppstoreAddOutlined,
  AppstoreOutlined,
  AudioOutlined,
  AudioMutedOutlined,
  AuditOutlined,
  BankOutlined,
  BarcodeOutlined,
  BarsOutlined,
  BellOutlined,
  BlockOutlined,
  BookOutlined,
  BorderOutlined,
  BranchesOutlined,
  BugOutlined,
  BuildOutlined,
  BulbOutlined,
  CalculatorOutlined,
  CalendarOutlined,
  CameraOutlined,
} from '@ant-design/icons';
import { getHTMLColorFormat } from '../../utils/property-utils';

export const antdIcons = [
  { key: "ArrowUpOutlined",label: <ArrowUpOutlined /> },
  { key: "ArrowDownOutlined", label: <ArrowDownOutlined /> },
  { key: "ArrowLeftOutlined", label: <ArrowLeftOutlined /> },
  { key: "ArrowRightOutlined", label: <ArrowRightOutlined /> },
  { key: "Arrow-45Outlined", label: <ArrowUpOutlined rotate={45}/> },
  { key: "Arrow-135Outlined", label: <ArrowUpOutlined rotate={135}/> },
  { key: "UpSquareOutlined", label: <UpSquareOutlined /> },
  { key: "DownSquareOutlined", label: <DownSquareOutlined /> },
  { key: "LeftSquareOutlined", label: <LeftSquareOutlined /> },
  { key: "RightSquareOutlined", label: <RightSquareOutlined /> },
  { key: "CheckCircleOutlined", label: <CheckCircleOutlined /> },
  { key: "CloseCircleOutlined", label: <CloseCircleOutlined /> },
  { key: "ExclamationCircleOutlined",label: <ExclamationCircleOutlined /> },
  { key: "CheckOutlined",label: <CheckOutlined /> },
  { key: "ExclamationOutlined", label: <ExclamationOutlined /> },
  { key: "CloseOutlined", label: <CloseOutlined /> },
  { key: "StarOutlined", label: <StarOutlined /> },
  { key: "StarFilled", label: <StarFilled /> },
  { key: "AreaChartOutlined", label: <AreaChartOutlined /> },
  { key: "PieChartOutlined", label: <PieChartOutlined /> },
  { key: "BarChartOutlined", label: <BarChartOutlined /> },
  { key: "DotChartOutlined", label: <DotChartOutlined /> },
  { key: "LineChartOutlined", label: <LineChartOutlined /> },
  { key: "HeatMapOutlined", label: <HeatMapOutlined /> },
  { key: "CaretUpOutlined", label: <CaretUpOutlined /> },
  { key: "CaretDownOutlined", label: <CaretDownOutlined /> },
  { key: "CaretLeftOutlined", label: <CaretLeftOutlined /> },
  { key: "CaretRightOutlined", label: <CaretRightOutlined /> },
]

const moreIcons = [
  { key: "FallOutlined", label: <FallOutlined /> },
  { key: "RiseOutlined", label: <RiseOutlined /> },
  { key: "StockOutlined", label: <StockOutlined /> },
  { key: "BoxPlotOutlined", label: <BoxPlotOutlined /> },
  { key: "FundOutlined", label: <FundOutlined /> },
  { key: "SlidersOutlined", label: <SlidersOutlined /> },
  { key: "StepBackwardOutlined", label: <StepBackwardOutlined /> },
  { key: "StepForwardOutlined", label: <StepForwardOutlined /> },
  { key: "FastBackwardOutlined", label: <FastBackwardOutlined /> },
  { key: "FastForwardOutlined", label: <FastForwardOutlined /> },
  { key: "ShrinkOutlined", label: <ShrinkOutlined /> },
  { key: "ArrowsAltOutlined", label: <ArrowsAltOutlined /> },
  { key: "DownOutlined", label: <DownOutlined /> },
  { key: "UpOutlined", label: <UpOutlined /> },
  { key: "LeftOutlined", label: <LeftOutlined /> },
  { key: "RightOutlined", label: <RightOutlined /> },
  { key: "DoubleLeftOutlined", label: <DoubleLeftOutlined /> },
  { key: "VerticalLeftOutlined", label: <VerticalLeftOutlined /> },
  { key: "VerticalRightOutlined", label: <VerticalRightOutlined /> },
  { key: "VerticalAlignTopOutlined", label: <VerticalAlignTopOutlined /> },
  { key: "VerticalAlignMiddleOutlined", label: <VerticalAlignMiddleOutlined /> },
  { key: "VerticalAlignBottomOutlined", label: <VerticalAlignBottomOutlined /> },
  { key: "ForwardOutlined", label: <ForwardOutlined /> },
  { key: "BackwardOutlined", label: <BackwardOutlined /> },
  { key: "RollbackOutlined", label: <RollbackOutlined /> },
  { key: "EnterOutlined", label: <EnterOutlined /> },
  { key: "RetweetOutlined", label: <RetweetOutlined /> },
  { key: "SwapOutlined", label: <SwapOutlined /> },
  { key: "SwapLeftOutlined", label: <SwapLeftOutlined /> },
  { key: "SwapRightOutlined", label: <SwapRightOutlined /> },
  { key: "PlayCircleOutlined", label: <PlayCircleOutlined /> },
  { key: "LogoutOutlined", label: <LogoutOutlined /> },
  { key: "MenuFoldOutlined", label: <MenuFoldOutlined /> },
  { key: "MenuUnfoldOutlined", label: <MenuUnfoldOutlined /> },
  { key: "AccountBookOutlined", label: <AccountBookOutlined /> },
  { key: "AimOutlined", label: <AimOutlined /> },
  { key: "AlertOutlined", label: <AlertOutlined /> },
  { key: "ApartmentOutlined", label: <ApartmentOutlined /> },
  { key: "ApiOutlined", label: <ApiOutlined /> },
  { key: "AppstoreAddOutlined", label: <AppstoreAddOutlined /> },
  { key: "AppstoreOutlined", label: <AppstoreOutlined /> },
  { key: "AudioOutlined", label: <AudioOutlined /> },
  { key: "AudioMutedOutlined", label: <AudioMutedOutlined /> },
  { key: "AuditOutlined", label: <AuditOutlined /> },
  { key: "BankOutlined", label: <BankOutlined /> },
  { key: "BarcodeOutlined", label: <BarcodeOutlined /> },
  { key: "BarsOutlined", label: <BarsOutlined /> },
  { key: "BellOutlined", label: <BellOutlined /> },
  { key: "BlockOutlined", label: <BlockOutlined /> },
  { key: "BookOutlined", label: <BookOutlined /> },
  { key: "BorderOutlined", label: <BorderOutlined /> },
  { key: "BranchesOutlined", label: <BranchesOutlined /> },
  { key: "BugOutlined", label: <BugOutlined /> },
  { key: "BuildOutlined", label: <BuildOutlined /> },
  { key: "BulbOutlined", label: <BulbOutlined /> },
  { key: "CalculatorOutlined", label: <CalculatorOutlined /> },
  { key: "CalendarOutlined", label: <CalendarOutlined /> },
  { key: "CameraOutlined", label: <CameraOutlined /> },
]

export const addDashes = (str) => {
  if (str==="none") return "None"
  return str.replace(/([A-Z])/g, '-$1').slice(1).toLowerCase();
}

export const getCardIconValues = (showMoreCardIcons) => {
  let icons = antdIcons
  if (showMoreCardIcons) {
    icons = [...icons , ...moreIcons]
  }
  icons = [...icons, { key: "none", label: "None" }]
  let cardIconValues = [];
  cardIconValues = icons.map((icon) => {
    return {
      key: icon.key,
      label: (
        <span>
          {icon.key !== "none" && icon.label} {addDashes(icon.key)}
        </span>
      ),
    };
  });
  return cardIconValues;
};

const CustomSVG = ({node}) => {
  return node
}
export const SVGDisplay = ({ svgString, color }) => {
  const node = <div style={{ color }} dangerouslySetInnerHTML={{ __html: svgString }} />;
  return <Icon component={() => <CustomSVG node={node}/>} style={{ color }}/>
};

export const getCardValue = (type = "", value = "", colorValue, showMoreCardIcons) => {
  let color = getHTMLColorFormat(colorValue)
  let icons = antdIcons
  if (showMoreCardIcons) {
    icons = [...icons , ...moreIcons]
  }
  if (type === "selectIcon") {
    const iconData = icons.find((icon) => value === icon.key) || [];
    if (iconData && iconData.key !=="none") {
      return <div style={{color}}> {iconData.label} </div>;
    }
  } else if (value && type === "url") {
    return <img src={value} style={{ height: "36px", width: "36px" }} />;
  } else if (value && type === "svg") {
    value.trim();
    return <SVGDisplay svgString={value} color={color}/>;
  } else if (value && type === "staticValue") {
    value.trim()
    return <span style={{color}}> {value} </span>;
  }
  return null;
};
