import { Tooltip } from "antd"


const HITooltip = props => {
    return (
        <Tooltip overlayClassName={props.customClassName || ''} title={props.title} placement={props.placement || "top"} overlayStyle={{ position: 'fixed', maxWidth: "100%" }}  >
            {props.children}
        </Tooltip>
    )
}

export default HITooltip