$(function () {

    const e = JSON.parse(document.getElementById("data").innerText);
    $("#status-switch").bootstrapSwitch('state', e.status === 'on');
    $("#status-switch").attr("value", e.status);

    new Vue({
        el: '#vue-div',
        data: {
            rss: e,
            version: e.version,
        },
        methods: {
            val: function (val) {
                return val;
            }
        }
    })

    $('#status-switch').on('switchChange.bootstrapSwitch', function (event, state) {
        $("#statusVal").attr("value", state ? "on" : "off");
    });

    $(".btn-info").click(function () {
        var formId = "ajax" + $(this).attr("id");
        $.post('update', $("#" + formId).serialize(), function (data) {
            if (data.success || data.status === 200) {
                $.gritter.add({
                    title: '  操作成功...',
                    class_name: 'gritter-success' + (!$('#gritter-light').get(0).checked ? ' gritter-light' : ''),
                });
            } else {
                $.gritter.add({
                    title: '  发生了一些异常...',
                    class_name: 'gritter-error' + (!$('#gritter-light').get(0).checked ? ' gritter-light' : ''),
                });
            }
        });
    });
});