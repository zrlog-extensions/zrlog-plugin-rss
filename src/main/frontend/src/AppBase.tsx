import {FunctionComponent} from "react";
import {RssInfoResponse} from "./index";
import AppMain from "./App";

export type AppBaseProps = {
    pluginInfo: RssInfoResponse;
}

const AppBase: FunctionComponent<AppBaseProps> = ({pluginInfo}) => {
    return <AppMain data={pluginInfo}/>;
}

export default AppBase;
