$(
    function () {
        var bean = {
            uri: "/foreNotice",
            notice: {}
        };
        var noticeVue = new Vue(
            {
                el: ".main",
                data: bean,
                mounted: function () {
                    this.list(0);
                },
                methods: {
                    list: function () {
                        var id = getUrlParms("nid");
                        var url = getPath() + this.uri + "/" + id + "?timeStamp=" + new Date().getTime();
                        axios.get(url).then(
                            function (value) {
                                if (value.code != '0') {
                                    location.href = getPath() + "/error";
                                }
                                noticeVue.notice = value.data;
                            }
                        );
                    }
                }
            });


    }
);