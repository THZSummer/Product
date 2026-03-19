# Retrieve app_ticket

The open platform will push the app_ticket event to the application every hour, and the event body contains app_ticket. The application can also actively call this interface to trigger the open platform to push the app_ticket event immediately. For event information, see [app_ticket events](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/application-v6/event/app_ticket-events).

## Request

Facts | &nbsp;
---|---
HTTP URL | https://open.feishu.cn/open-apis/auth/v3/app_ticket/resend
HTTP Method | POST
Supported app types | Store App
Required scopes<br>**To use this API, you must have at least 1 of the listed scopes.** | None

### Request header

Parameter | Type | Required | Description
---|---|---|---
Content-Type | string | Yes | **Fixed value**: "application/json; charset=utf-8"

### Request body

Parameter | Type | Required | Description
---|---|---|---
app_id | string | Yes | The unique identifier of the application is obtained after the application is created. For a detailed introduction to `app_id`, please refer to the introduction of [General parameters](https://open.feishu.cn/document/ukTMukTMukTM/uYTM5UjL2ETO14iNxkTN/terminology).<br>**Example value:** "cli_slkdjalasdkjasd"
app_secret | string | Yes | Application key, obtained after creating the application. For a detailed introduction to `app_secret`, please refer to the introduction of [General parameters](https://open.feishu.cn/document/ukTMukTMukTM/uYTM5UjL2ETO14iNxkTN/terminology).<br>**Example value:** "dskLLdkasdjlasdKK"

### Request body example

```json
{
    "app_id": "cli_slkdjalasdkjasd",
    "app_secret": "dskLLdkasdjlasdKK"
}
```

## Response

### Response body

Parameter | Type | Description
---|---|---
code | int | Error codes, fail if not zero
msg | string | Error descriptions

### Response body example

```json
{
    "code": 0,
    "msg": "success"
}
```

### Error code
For a detailed description of error codes, see Introduction to [Generic error code](https://open.feishu.cn/document/ukTMukTMukTM/ugjM14COyUjL4ITN).
