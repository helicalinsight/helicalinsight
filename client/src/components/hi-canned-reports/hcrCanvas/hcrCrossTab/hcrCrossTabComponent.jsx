import HIIcon from '../../../common/icons/hi-icons';
import HCRCrossTab from './hcrCrossTab';

const HCRCrossTabComponent = (props) => {
    const { isElementRender, label = "Cross Tab", width } = props;
    return (
        <div>
            {isElementRender ? (
                <div style={{ display: 'flex', width, gap: 10 }}>
                    <HIIcon name="hi-hcr-crosstab" />
                    <div>
                        <span style={{ height: 20 }}>{label}</span>
                    </div>
                </div>
            ) : (
                <HCRCrossTab {...props} />
            )}
        </div>
    );
}

export default HCRCrossTabComponent;