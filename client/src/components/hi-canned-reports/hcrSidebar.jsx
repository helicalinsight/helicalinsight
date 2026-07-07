import HcrCanvasSidebar from "./hcrCanvas/hcrCanvasSidebar";
import HcrDSSidebar from "./hcrDataSource/hcrDSSidebar";

export default function HcrSidebar({ selectedTab }) {
    if (selectedTab === 'datasource') {
        return <HcrDSSidebar />
    }
    if (selectedTab === 'canvas') {
        return <HcrCanvasSidebar />
    }
    return null;
}

