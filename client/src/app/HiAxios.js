import { v4 as uuidv4 } from "uuid";
import axios from "axios";
import { handleAxiosObject } from "./helperMethods";
import notify from "../components/hi-notifications/notify";
import { hiMockAxios } from "./mock-axios";
import { publish, subscribe } from "./custom-events";
class HIAxios {
  constructor(auth, baseURL, dispatch, successNotification) {
    this.successNotification = successNotification;
    this.auth = auth;
    this.baseURL = baseURL;
    this.dispatch = dispatch;
    this.data = {};
    this.controller = new AbortController();
    HIAxios.requestId = this.generateRequestId();
    this.CancelToken = axios?.CancelToken || {
      source: () => {},
      cancel: () => {},
    };
    this.source = this.CancelToken.source();
    this.hiAxios = undefined;
    this.url = undefined;
    this.Notify = notify(dispatch);
    HIAxios.apiCalls= 0;
    this.instance =
      process.env.NODE_ENV === "test"
        ? hiMockAxios().instance
        : axios.create(
            handleAxiosObject({
              cancelToken: this.source.token,
              signal: this.controller.signal,
              auth: this.auth,
              baseURL: this.baseURL,
              dispatch: this.dispatch,
              successNotification: this.successNotification
            })
          );
         
    this.triggerAPITracking()
  }
  triggerAPITracking=()=>{
  this.instance?.interceptors?.request.use((config) => {
    // if(HIAxios.apiCalls===0){
      
      
      // }
    // Increment the API call counter
    HIAxios.apiCalls=HIAxios.apiCalls+1;
    publish("hiReadyState",{apiCalls : HIAxios.apiCalls,
      changeTime : new Date(),
      status : "started", 
      });
    // console.log(hiReadyState.detail)
    return config;
  }, (error) => {
    return Promise.reject(error);
  });

  // Add a response interceptor
  this.instance?.interceptors?.response.use((response) => {
    // Decrement the API call counter
    if(HIAxios.apiCalls>0){
    HIAxios.apiCalls=HIAxios.apiCalls-1;
    }
    publish("hiReadyState",{apiCalls : HIAxios.apiCalls,
      changeTime : new Date(),
      status : "completed", 
      });

    return response;
  }, (error) => {
    if(HIAxios.apiCalls>0){
      HIAxios.apiCalls=HIAxios.apiCalls-1;
      }
    return Promise.reject(error);
  });
}

  // sendHiReadyState=()=>{
  //   if(this.apiCalls===0){
  //     this.dispatch(appActions.updateHIReadyState("completed"))
  //   }
  // }

  generateRequestId = () => {
    this.requestId = uuidv4();
    return this.requestId;
  };

  getRequestId = () => {
    return this.requestId;
  };

  abort = (prop = {}) => {
    let {setIsLoading, table, setLoading,setIsAborted} = prop;
    this.dispatch((dispatch, getState, services) => {
      this.hiAxios = services(dispatch);
    });
    this.hiAxios.instance
      .get("/cancelRequest", {
        params: { requestId: this.requestId },
      })
      .then((res) => {
        if (typeof res.data === "string") {
          try {
            this.data = JSON.parse(res.data);
          } catch (e) {
            return;
          }
        }
        if (this.data?.status === 1) {
          setIsAborted && setIsAborted(true);
        }
        if (this.data?.response) {
          // this.Notify.success({
          //   type: "Network Call",
          //   ...this.data?.response,
          // });
        }
        setIsLoading && setIsLoading((state) => ({
          ...state,
          [table]: false,
          refreshBtn: false,
        }));
        setLoading && setLoading(false)
      })
      .catch((error) => {
        console.log(error);
        // notify(this.dispatch).error({
        //   type: "Network Call",
        //   message: error.message,
        // });
      });
    this.controller.abort();
    // this.source.cancel();
  };
}
export default HIAxios;
