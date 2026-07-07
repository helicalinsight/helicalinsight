import React from "react";
import { cx } from "../../utils/common-utils";
import "./ib-space.scss";

const IBSpace = ({
    children,
    space = "0",
    as: Component = "div",
    stack = "horizontal",
    alignItem,
    flexWrap = "nowrap",
    justifyContent,
    flexDirection = stack === "horizontal" ? "row" : "column",
    className,
    ...rest
}) => {
    const spaceClassname = cx({
        "ib-space": true,
        [`ib-space__${stack}`]: true,
        [`ib-space__${stack}__${flexDirection}`]: true,
        [`ib-space__${stack}__${flexDirection}__${space}`]: true,
        [`ib-space__align-item--${alignItem}`]: Boolean(alignItem),
        [`ib-space__justify-content--${justifyContent}`]: Boolean(justifyContent),
        [`ib-space__flex-wrap--${flexWrap}`]: true,
        [className]: Boolean(className),
    });

    return (
        <Component className={spaceClassname} {...rest}>
            {children}
        </Component>
    );
};

export default IBSpace;
