import HIIcon from '../../../common/icons/hi-icons';
import HCRCrosstabViewMode from '../advanceComponents/crosstab/hcrCrosstabViewMode';
import "./hcrCrosstab.scss";

const ctTemplate = (
    <table className="crosstab-template">
        <thead>
            <tr>
                <th></th>
                <th></th>
                <th></th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td></td>
                <td></td>
                <td></td>
            </tr>
            <tr>
                <td></td>
                <td></td>
                <td></td>
            </tr>
        </tbody>
    </table>
)

const HCRCrossTabComponentV2 = (props) => {
    const { isElementRender, label = "Cross Tab", width, data = {} } = props;
    const { isCTConstructed } = data || {};

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
                isCTConstructed ? <HCRCrosstabViewMode data={data} /> : ctTemplate
            )}
        </div>
    );
}

export default HCRCrossTabComponentV2;