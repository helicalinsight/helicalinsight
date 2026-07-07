import React, { Component } from 'react';
import { HIResults } from '../components';
import { connect } from 'react-redux';

class ErrorBoundry extends Component {

    constructor(props) {
        super(props);
        this.state = {
            isError: false
        };
    }

    static getDerivedStateFromError(error) {
        console.error('Error caught in ERROR BOUNDRY', error)
        return {
            isError: true
        }
    }

    componentDidUpdate(prevProps) {
        if (this.props.activeRoute !== prevProps.activeRoute) {
            this.setState({ isError: false });
        }
    }

    // componentDidCatch(error, info) {
    //     // console.log(error);
    //     // console.log(info);
    // }

    render() {
        // if (this.state.isError) { // commenting this as per discussion with Nitin
        //     return <HIResults btnContent = "Go Back" status="error" />
        // }
        return this.props.children
    }
}

const mapStateToProps = (state) => ({
    activeRoute: state.app.activeRoute,
})

export default connect(mapStateToProps)(ErrorBoundry)