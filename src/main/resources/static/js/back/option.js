$(
    function () {
        var system = {
            uri1:"/admin/options",
            uri2:"/admin/logs",
            options:[{id:0,key:'',value:''},{id:0,key:'',value:''},{id:0,key:'',value:''},{id:0,key:'',value:''}],
            option:{},
            logs:[],
            pages:[],
            log:{},
            key:'',
            image:null
        };
        var systemVue = new Vue(
            {
                el:".container",
                data:system,
                mounted:function () {
                    this.getOption();
                    this.list(0);
                },
                methods:{
                    list:function (start) {
                        var url = getPath() + this.uri2 + "?start=" + start;
                        axios.get(url).then(
                            function (value) {
                                if (value.data.content.length > 0) {
                                    systemVue.pages = value.data;
                                    systemVue.logs = value.data.content;
                                    $(".back_option_list_table").show();
                                    $(".notfound_list").hide();
                                    $(".pageDiv").show();
                                    $(".notfound_search").hide();
                                }
                                else {
                                    $(".back_option_list_table").hide();
                                    $(".notfound_list").show();
                                    $(".pageDiv").hide();
                                    $(".notfound_search").hide();
                                }
                            }
                        )
                    },
                    jump:function (page) {
                        jump(page,systemVue);
                    },
                    jumpByNumber:function (start) {
                        jumpByNumber(start,systemVue);
                    },
                    getOption:function () {
                        var url = getPath() + this.uri1 ;
                        axios.get(url).then(
                            function (value) {
                                systemVue.options=value.data;
                            }
                        );
                    },
                    reset:function () {
                        location.reload();
                    },
                    updateOption:function(id,name,e){
                        systemVue.option = {id:id,key:name,value:$(e.target).val()};
                        var url = getPath() + this.uri1+"/"+id;
                        axios.put(url, systemVue.option).then(
                            function (value) {
                                console.log(value);
                            }
                        );
                    },
                    getFile:function (e) {
                        this.image = e.target.files[0];
                        if (!checkEmpty(this.image, '图标')) {
                            return;
                        }
                        var formData = new FormData();
                        formData.append("image", this.image);
                        var url = getPath() + this.uri1+"/image";
                        axios.put(url, formData).then(
                            function (value) {
                                systemVue.image = null;
                                location.reload();
                            }
                        );
                    },
                    search:function () {
                        if (!checkEmpty(this.key, '关键词')) {
                            return;
                        }
                        if (this.key.length >= 10) {
                            alert("关键词长度不能大于十，请重新搜索")
                            return;
                        }
                        var key = this.key;
                        var url = getPath() + systemVue.uri2 + "/search?key=" +key;
                        axios.post(url).then(
                            function (value) {
                                $(".pageDiv").hide();
                                if (value!=null&&value.data.length > 0) {
                                    systemVue.logs = value.data;
                                    $(".back_option_list_table").show();
                                    $(".notfound_search").hide();
                                }
                                else {
                                    $(".back_option_list_table").hide();
                                    $(".notfound_search").show();
                                }
                            }
                        );
                    }
                }
            }
        );
    }
);