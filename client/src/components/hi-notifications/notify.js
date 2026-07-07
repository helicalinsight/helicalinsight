import { v4 as uuidv4, validate } from "uuid";
import { updateNotificationData } from "../../redux/actions/useractions.actions";
import { message } from "antd";
import { isObject } from "../../utils/is-object";

const validateInput = (args) => {
  let obj = {}
  if (args.length === 1 && isObject(args[0])) {
    obj = args[0];
  }
  else if (args.length === 2 && typeof args[0] === 'string' && typeof args[1] === 'string') {
    obj = {
      message: args[1],
      type: args[0]
    }
  }
  return obj
}

const notify = (dispatch) => {
  const safeDispatch = typeof dispatch === "function" ? dispatch : () => {};
  const success = (...args) => {
    let obj = validateInput(args);
    const successItem = {
      id: uuidv4(),
      status: "success",
      message: obj?.message || '',
      type: obj?.type,
    };

    message.success(
      {
        content: obj?.message || '',
        style: {
          width: "70%",
          position: "relative",
          left: "50%",
          top: "25px",
          transform: `translateX(${-50}%)`,
        },
      },
      2.5
    );

    safeDispatch(updateNotificationData(successItem));
  };
  const error = (...args) => {
    let obj = validateInput(args);
    const failureItem = {
      id: uuidv4(),
      status: "error",
      message: obj.message || '',
      type: obj?.type,
    };
    message.error({ content: obj?.message || '' });
    safeDispatch(updateNotificationData(failureItem));
  };

  const info = (...args) => {
    let obj = validateInput(args);
    const infoItem = {
      id: uuidv4(),
      status: "info",
      message: obj.message || '',
      type: obj?.type,
    };
    message.info({ content: obj?.message || '' });
    safeDispatch(updateNotificationData(infoItem));
  };
  const loading = (...args) => {
    let obj = validateInput(args);
    const loadingItem = {
      id: uuidv4(),
      status: "info",
      message: obj.message || '',
      type: obj?.type,
    };
    message.loading({ content: obj?.message || '' });
    safeDispatch(updateNotificationData(loadingItem));
  };

  const warning = (...args) => {
    let obj = validateInput(args);
    const warningItem = {
      id: uuidv4(),
      status: "warn",
      message: obj.message || '',
      type: obj?.type,
    };
    message.warning({ content: obj?.message || '' });
    safeDispatch(updateNotificationData(warningItem));
  };
  return { warning, loading, info, error, success, warn: warning };
};
// const notify = { success, error, info, loading, warning };
// window.notify = notify;
export default notify;
