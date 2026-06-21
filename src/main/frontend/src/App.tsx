import {
    Button,
    Card,
    Form,
    Grid,
    Input,
    Typography,
    message,
    theme,
    Alert
} from "antd";
import axios from "axios";
import {FunctionComponent, useState} from "react";
import styled from "styled-components";
import {RssInfoResponse, StandardResponse} from "./index";
import {CopyOutlined, SaveOutlined, GlobalOutlined, InfoCircleOutlined} from "@ant-design/icons";

const {Paragraph, Text} = Typography;

export type AppProps = {
    data: RssInfoResponse;
}

type RssFormValues = {
    uriPath: string;
    rssText: string;
}

const requestUpdate = async (params: Record<string, string>) => {
    const {data} = await axios.post<StandardResponse<any>>("update", new URLSearchParams(params), {
        headers: {"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"},
    });
    return data;
};

const Shell = styled.div`
  width: 100%;
  max-width: 960px;
  margin: 0 auto;
  padding: 24px 16px;
  box-sizing: border-box;

  @media (max-width: 1024px) {
    padding: 16px;
  }

  @media (max-width: 575px) {
    padding: 12px;
  }
`;

const HeaderSection = styled.div`
  margin-bottom: 24px;

  @media (max-width: 575px) {
    margin-bottom: 16px;
  }
`;

const Title = styled.h1`
  margin: 0;
  font-size: 26px;
  line-height: 34px;
  font-weight: 700;
  letter-spacing: 0;
`;

const SubTitle = styled.div<{ $token: any }>`
  margin-top: 8px;
  color: ${props => props.$token.colorTextDescription};
  font-size: 14px;
  line-height: 20px;
`;

const FormCard = styled(Card)<{ $token: any }>`
  border: 1px solid ${props => props.$token.colorBorderSecondary};
  border-radius: ${props => props.$token.borderRadiusLG}px;
  margin-bottom: 24px;

  .ant-card-body {
    @media (max-width: 575px) {
      padding: 16px;
    }
  }
`;

const SectionTitle = styled.h3<{ $token: any }>`
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: ${props => props.$token.colorTextHeading};
  display: flex;
  align-items: center;
  gap: 8px;
`;

const PreviewFrameWrapper = styled.div<{ $token: any }>`
  border: 1px dashed ${props => props.$token.colorBorder};
  border-radius: 8px;
  padding: 16px;
  background: ${props => props.$token.colorBgContainerSecondary || 'rgba(0,0,0,0.01)'};
  display: flex;
  justify-content: flex-start;
  align-items: center;
  min-height: 80px;
`;

const CodeBlock = styled.div<{ $token: any }>`
  background: ${props => props.$token.colorFillTertiary};
  border: 1px solid ${props => props.$token.colorBorder};
  border-radius: 6px;
  padding: 10px 14px;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, Courier, monospace;
  font-size: 13px;
  color: ${props => props.$token.colorTextDescription};
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 8px;

  code {
    min-width: 0;
    overflow-wrap: anywhere;
  }

  @media (max-width: 575px) {
    align-items: stretch;
    flex-direction: column;
  }
`;

const App: FunctionComponent<AppProps> = ({data}) => {
    const {token} = theme.useToken();
    const screens = Grid.useBreakpoint();
    const isPhone = Boolean(screens.xs && !screens.sm);
    const [rss, setRss] = useState<RssFormValues>({
        uriPath: data.uriPath || "feed",
        rssText: data.rssText || "",
    });
    const [loading, setLoading] = useState(false);
    const [previewKey, setPreviewKey] = useState(0);
    const [form] = Form.useForm<RssFormValues>();
    const [messageApi, contextHolder] = message.useMessage();

    const handleCopy = () => {
        navigator.clipboard.writeText('<plugin name="rss" view="widget"/>');
        messageApi.success("已复制到剪贴板");
    };

    const handleSave = async () => {
        try {
            const values = await form.validateFields();
            setLoading(true);
            const response = await requestUpdate({
                uriPath: values.uriPath,
                rssText: values.rssText,
            });

            if (response.success) {
                messageApi.success("设置保存成功");
                setRss(values);
                // Force reload preview iframe
                setPreviewKey(prev => prev + 1);
            } else {
                messageApi.error(response.message || "更新失败，请重试");
            }
        } catch (e) {
            messageApi.error(e instanceof Error ? e.message : "参数验证失败");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Shell>
            {contextHolder}

            <HeaderSection>
                <Title>RSS 订阅设置</Title>
                <SubTitle $token={token}>配置 RSS 文件路径和小工具文案，插件会生成 Feed XML 订阅源</SubTitle>
            </HeaderSection>

            <Form form={form} layout="vertical" initialValues={rss} onFinish={handleSave}>
                <FormCard $token={token}>
                    <SectionTitle $token={token}>
                        <GlobalOutlined /> 基本设置
                    </SectionTitle>

                    <Form.Item
                        label="文件存放地址"
                        name="uriPath"
                        tooltip="Feed XML 文件的相对地址目录，例如输入 feed，则订阅源可通过 /feed 获取"
                        rules={[{required: true, message: "请输入文件存放地址"}]}
                    >
                        <Input size="large" placeholder="例如：feed" maxLength={120} />
                    </Form.Item>

                    <Form.Item
                        label="说明文字"
                        name="rssText"
                        tooltip="显示在小工具/侧边栏 RSS 订阅链接旁的文本内容"
                    >
                        <Input.TextArea
                            rows={4}
                            size="large"
                            placeholder="例如：订阅本站最新内容以获取即时推送"
                            maxLength={500}
                        />
                    </Form.Item>

                    <Alert
                        message="提示"
                        description={
                            <span>
                                保存设置后，RSS 插件会更新 XML 数据源；文章发布或修改后会重新生成订阅文件，订阅源可通过地址
                                <Text code>{`/${form.getFieldValue("uriPath") || rss.uriPath}`}</Text> 访问
                            </span>
                        }
                        type="info"
                        showIcon
                        style={{marginBottom: 16}}
                    />

                    <Form.Item style={{margin: 0, textAlign: 'right'}}>
                        <Button
                            type="primary"
                            icon={<SaveOutlined />}
                            size="large"
                            loading={loading}
                            htmlType="submit"
                            style={isPhone ? {width: "100%"} : {minWidth: 120}}
                        >
                            保存设置
                        </Button>
                    </Form.Item>
                </FormCard>
            </Form>

            <FormCard $token={token}>
                <SectionTitle $token={token}>
                    <InfoCircleOutlined /> 挂载与预览
                </SectionTitle>

                <div style={{marginBottom: 20}}>
                    <Paragraph>
                        如需在站点侧边栏或小工具区域显示 RSS 订阅入口，可在主题或页面中插入以下插件标记：
                    </Paragraph>
                    <CodeBlock $token={token}>
                        <code>&lt;plugin name="rss" view="widget"/&gt;</code>
                        <Button
                            type="text"
                            size="small"
                            icon={<CopyOutlined />}
                            onClick={handleCopy}
                            style={isPhone ? {width: "100%"} : undefined}
                        >
                            复制
                        </Button>
                    </CodeBlock>
                </div>

                <SectionTitle $token={token} style={{fontSize: 14, fontWeight: 550}}>
                    实时预览效果
                </SectionTitle>
                <PreviewFrameWrapper $token={token}>
                    <iframe
                        key={previewKey}
                        src="/p/rss/widget?preview=true"
                        title="RSS Widget Preview"
                        style={{
                            height: "48px",
                            width: "100%",
                            maxWidth: "320px",
                            border: "0",
                            background: "transparent"
                        }}
                    />
                </PreviewFrameWrapper>
            </FormCard>
        </Shell>
    );
};

export default App;
