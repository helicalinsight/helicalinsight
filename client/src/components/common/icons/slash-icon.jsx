import Icon from "@ant-design/icons";

// Rounded frame open at the top-right corner, with a slash inside and sparkles in that gap.
const SlashSvg = () => (
  <svg viewBox="0 0 24 24" width="1em" height="1em" fill="none" aria-hidden="true">
    <path
      d="M17.4 13.2V18A3.6 3.6 0 0 1 13.8 21.6H4.6A3.6 3.6 0 0 1 1 18V8.8A3.6 3.6 0 0 1 4.6 5.2H9"
      stroke="currentColor"
      strokeWidth="1.25"
      strokeLinecap="round"
      strokeLinejoin="round"
      fill="none"
    />
    <path d="M10.3 11.2L7.8 16.2" stroke="currentColor" strokeWidth="1.85" strokeLinecap="round" fill="none" />
    <path fill="currentColor" d="M19.5 3.25L20.26 5.84L22.85 6.6L20.26 7.36L19.5 9.95L18.74 7.36L16.15 6.6L18.74 5.84Z" />
    <path fill="currentColor" d="M15.15 0.7L15.57 2.13L17 2.55L15.57 2.97L15.15 4.4L14.73 2.97L13.3 2.55L14.73 2.13Z" />
  </svg>
);

const SlashIcon = (props) => <Icon component={SlashSvg} {...props} />;

export default SlashIcon;
