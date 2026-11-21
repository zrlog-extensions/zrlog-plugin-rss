import {useEffect, useState} from "react";
import {createRoot} from "react-dom/client";
import axios from "axios";
import AppBase from "./AppBase";
import {PluginThemeProvider} from "./PluginThemeProvider";
import type {PluginAppearance} from "./PluginThemeProvider";

export interface Plugin {
    id: string;
    version: string;
    name: string;
    paths: string[];
    actions: string[];
    desc: string;
    author: string;
    shortName: string;
    indexPage: string;
    previewImageBase64: string;
    services: string[];
    dependentService: string[];
}

export interface RssInfoResponse extends PluginAppearance {
    dark: boolean;
    colorPrimary?: string;
    adminColorPrimary?: string;
    plugin: Plugin;
    uriPath: string;
    rssText: string;
}

export interface StandardResponse<T> {
    success: boolean;
    message?: string;
    data: T;
}

const loadFromDocument = () => {
    try {
        const node = document.getElementById("pluginInfo");
        if (node === null || node.innerText.length === 0) {
            return null;
        }
        return JSON.parse(node.innerText) as StandardResponse<RssInfoResponse>;
    } catch (e) {
        return null;
    }
}

const Index = () => {
    const [response, setResponse] = useState<StandardResponse<RssInfoResponse> | null>(loadFromDocument);

    useEffect(() => {
        if (response === null) {
            axios.get<StandardResponse<RssInfoResponse>>("json").then(({data}) => {
                setResponse(data);
            });
        }
    }, [response]);

    if (response === null || !response.success) {
        return <></>;
    }

    return (
        <PluginThemeProvider page={response.data}>
            <AppBase pluginInfo={response.data}/>
        </PluginThemeProvider>
    );
};

const container = document.getElementById("app");
const root = createRoot(container!);
root.render(<Index/>);
