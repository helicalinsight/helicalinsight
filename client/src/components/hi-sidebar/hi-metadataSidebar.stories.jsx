import React from 'react';
import {HIMetadatSidebar} from './hi-metadataSidebar'
import "antd/dist/antd.css";

export default {
    /* 👇 The title prop is optional.
    * See https://storybook.js.org/docs/react/configure/overview#configure-story-loading
    * to learn how to generate automatic titles
    */
    title: 'Metadata-side-bar',
    component: HIMetadatSidebar,
    // argTypes: {
    //     variant: {
    //         options: ['primary', 'secondary'],
    //         control: { type: 'radio' },
    //     },
    // },
};

const Template = (args) => <HIMetadatSidebar {...args} />

// export const MSideBar = () => <HIMetadatSidebar options />
export const Default = Template.bind({});
Default.args = {
    foo : {a : 12},
    123 : '54'
}