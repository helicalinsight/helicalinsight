
import { isEqual } from "lodash-es";
import { connect } from "react-redux";
import React from "react";
import notify from "../components/hi-notifications/notify";

class ErrorFallback extends React.Component{
    state = {
      error:false,
    }
    componentDidCatch(error, info) {
      if(error){
        if(this.props.dispatch){
          let { errMessage } = this.props
          // const Notify = notify(this.props.dispatch);
          // Notify.error({type:"frontend",message:`${error.message}.${errMessage}`})
        }
        this.setState({error: error});
      }
    }
    componentDidUpdate(prevProps){
        if(this.state.error && !isEqual(prevProps,this.props)){
            this.setState({error:false})
        }
    }
    render(){
      if(this.state.error){
        return <div> {this.props.errTemplate || "Something Went Wrong"} </div>
      }
      return(
        <>{this.props.children}</>
      )
    }
  }
  
  export default connect(null,null)(ErrorFallback);
