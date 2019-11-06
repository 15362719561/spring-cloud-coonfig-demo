var app = new Vue({
    el: '#app',
    data: {
        ctx: ctx,
        division: {
            layerSearch: '', //搜索关键字
            layers: [],
            urls: [],
            layersTableLoading: false,
            layersTableHeight:  window.document.body.clientHeight - 150,
            rows: 10,
            total: null,
            currentPage: 1,
            layerImportDialogVisible: false,
            importForm: {
                cityUrl: '',
                cityName: '',
                streetUrl: '',
                streetName: '',
                countryUrl: '',
                countryName:''
            },
            cityLayerFields: [],
            citySelectedFieldValue: '',
            citySelectedNameValue: '',
            streetLayerFields: [],
            streetSelectedFieldValue: '',
            streetSelectedNameValue: '',
            countryLayerFields:[],
            countrySelectedFieldValue: '',
            countrySelectedNameValue: '',
            importDataLoading: false,
            loadLayerResourceLoading: false,
            loadLayerResourceDialogVisible: false,
            importDataLoadingTip: '导入数据中...',
            loadLayerResourceLoadingTip: '数据资源加载中...',
            importType: '',
            resourceLayers: [],
            resourceLayersCurrentPage: 1,
            resourceLayersRows: 10,
            resourceLayersSearch: '',
            resourceLayersTotal: null,
            mapType: null,
            currentLayerType: ''
        }
    },
    methods: {

        /**
         * 加载行政区划图层配置
         */
        loadLayers: function(){
            var vm = this;
            vm.division.layersTableLoading = true;
            var param = {
                page: vm.division.currentPage,
                rows: vm.division.rows,
                name: vm.division.layerSearch
            };
            var getLayers = function(){
                $.ajax({
                    url:  ctx + '/agsupport/division/findList',
                    type: 'get',
                    dataType:'json',
                    data: param,
                    async: true,
                    success: function(result){
                        result.rows.forEach(function(item){  //初始化行的编辑状态
                            item.editStatus = false;
                            item.url = vm.urlItemFormatter(item);
                        });
                        Vue.set(vm.division,'layers',result.rows);
                        if(vm.division.total === null) {
                            vm.division.total = result.total;
                        }
                        vm.division.layersTableLoading = false;
                    },
                    error: function () {
                        vm.division.layersTableLoading = false;
                    }
                });
            }
            this.loadUrlList(getLayers);
        },

        /**
         * 关键字查询
         * @param value
         */
        searchKeyChange: function (value) {
             this.division.rows = 10;
             this.division.total = null;
             this.division.currentPage = 1;
             this.loadLayers();
        },

        /**
         * 清空查询
         */
        clearSearch: function () {
            this.division.layerSearch = '';
            this.division.rows = 10;
            this.division.total = null;
            this.division.currentPage = 1;
            this.loadLayers();
        },

        /**
         * 地图图层关键字查询
         */
        resourceLayerSearchKeyChange: function () {
            this.division.resourceLayersRows = 10;
            this.division.resourceLayersTotal = null;
            this.division.resourceLayersCurrentPage = 1;
            this.loadResourceLayers();
        },

        /**
         * 清空地图图层关键字查询
         */
        clearResourceLayerSearch: function () {
            this.division.resourceLayersSearch = '';
            this.division.resourceLayersRows = 10;
            this.division.resourceLayersTotal = null;
            this.division.resourceLayersCurrentPage = 1;
            this.loadResourceLayers();
        },

        /**
         * 获取url数据
         */
        loadUrlList: function (callback) {
            var vm  = this;
            $.ajax({
                url: ctx + '/agsupport/division/getAllUrlList',
                type: 'get',
                async: true,
                dataType: "json",
                success: function (result) {
                    if(result.success){
                        result.message = JSON.parse(result.message);
                        var urls = [];
                        result.message.forEach(function(item){
                            var data = {id: item.id, url: item.url};
                            urls.push(data);
                        });
                        Vue.set(vm.division,'urls',urls);
                        callback();
                    }
                },
                error: function (e) {
                    vm.$message({
                        showClose: true,
                        message: '获取数据出错！',
                        type: 'error'
                    });
                }
            });
        },

        /**
         * 格式化url
         * @param row
         * @param column
         * @returns {*}
         */
        urlItemFormatter: function (row, column) {
            var filterReuslt =  this.division.urls.filter(function(item){
                return item.id == row.refUrl;
            });
            if(filterReuslt.length === 1) {
                return filterReuslt[0].url;
            }else {
                return row.refUrl;
            }
        },

        /**
         * 根据url匹配id
         * @param row
         * @returns {*}
         */
        getUrlItemId: function (row) {
            var filterReuslt =  this.division.urls.filter(function(item){
                return item.url == row.url;
            });
            if(filterReuslt.length === 1) {
                return filterReuslt[0].id;
            }else {
                return row.refUrl;
            }
        },

        /**
         * 切换分页
         * @param page
         */
        handleLayersPageChange: function (page) {
            this.division.currentPage = page;
            this.loadLayers();
        },

        /**
         * 切换分页
         * @param page
         */
        handleResourceLayersPageChange: function (page) {
            this.division.resourceLayersCurrentPage = page;
            this.loadResourceLayers();
        },

        /**
         * 打开图层导入窗口
         */
        showLayerImportDialog: function () {
            this.division.layerImportDialogVisible = true;
        },

        /**
         * 打开图层编辑窗口
         */
        showEditLayerView: function (row) {
            row.editStatus = true;
        },

        /**
         * 退出编辑状态
         * @param row
         */
        cencelEditLayerView: function (row) {
            row.editStatus = false;
            this.loadLayers();
        },

        /**
         * url选择事件
         */
        urlSelChange: function (value,row) {
            var id = this.getUrlItemId(row);
            row.refUrl = id;
        },

        /**
         * 保存图层
         * @param row
         */
        saveLayer: function (row) {
            var vm = this;
            if(row.code === '' || row.pcode === '' || row.name === '' || row.refUrl === '') {
                this.$alert('图层信息不能为空！', '系统提示', {
                    confirmButtonText: '确定'
                });
                return;
            }
            var list = [row];
            $.ajax({
                url: ctx + '/agsupport/division/saveOrUpdate',
                type: 'post',
                data: {listStr: JSON.stringify(list)},
                async: false,
                dataType: "json",
                success: function (result) {
                    if (result.success) {
                        vm.$message({
                            showClose: true,
                            message: '保存成功！',
                            type: 'success'
                        });
                        row.editStatus = false;
                    } else {
                        vm.$message({
                            showClose: true,
                            message: '保存失败！',
                            type: 'error'
                        });
                    }
                },
                error: function (e) {
                    vm.$message({
                        showClose: true,
                        message: '保存失败！',
                        type: 'error'
                    });
                }
            });
        },

        /**
         * 批量删除图层
         */
        deleteLayers: function(){
            var selection = this.$refs.divisionLayerTable.selection;
            if(selection.length === 0) {
                this.$alert('请选择要删除的数据！', '系统提示', {
                    confirmButtonText: '确定'
                });
                return;
            }
            var ids = [];
            selection.forEach(function(layer){
                ids.push(layer.id);
            });
            var vm = this;
            this.$confirm('此操作将永久删除当前选中图层, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(function(){
                $.post(ctx + '/agsupport/division/delete', {paramIds: ids.join(',')}, function (result) {
                    if (result.success) {
                        vm.$message({
                            showClose: true,
                            message: '删除成功！',
                            type: 'success'
                        });
                        vm.loadLayers();
                    }
                }, 'json');
            });

        },

        /**
         * 新增图层
         */
        addLayer: function () {
            var sortId = this.division.layers.length + 1;
            var layer = {code: "", pcode: "",name: "", refUrl: "",sortId: sortId,editStatus: true};
            this.division.layers.unshift(layer);
        },

        /**
         * 导入
         */
        importData: function () {
            var vm = this;
            if(vm.division.importForm.cityUrl.trim() === ''
                && vm.division.importForm.streetUrl.trim() === ''
                && vm.division.importForm.countryUrl.trim() === '') {
                vm.$alert('请至少填入一级导入的图层信息！', '系统提示', {
                    confirmButtonText: '确定'
                });
                return;
            }
            this.$confirm('此操作将删除当前数据表中的所有数据，是否继续导入?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(function(){
                vm.division.importDataLoading = true;
                $.ajax({
                    type: "get",
                    url: ctx + "/agsupport/division/importLayers",
                    data: {
                        layerType: vm.division.currentLayerType,
                        cityUrl:vm.division.importForm.cityUrl,
                        cityLayerName:vm.division.importForm.cityName,
                        cityNameField:vm.division.citySelectedNameValue,
                        cityCodeField:vm.division.citySelectedFieldValue,
                        streetUrl:vm.division.importForm.streetUrl,
                        streetLayerName:vm.division.importForm.streetName,
                        streetNameField:vm.division.streetSelectedNameValue,
                        streetCodeField:vm.division.streetSelectedFieldValue,
                        countryUrl:vm.division.importForm.countryUrl,
                        countryLayerName:vm.division.importForm.countryName,
                        countryNameField:vm.division.countrySelectedNameValue,
                        countryCodeField:vm.division.countrySelectedFieldValue
                    },
                    async: true,
                    dataType: "json",
                    beforeSend: function(XMLHttpRequest){

                    },
                    success: function(result){
                        if (result.success) {
                            vm.$message({
                                showClose: true,
                                message: '导入成功！',
                                type: 'success'
                            });
                            vm.division.currentPage = 1;
                            vm.division.rows = 10;
                            vm.loadLayers();
                            vm.division.importDataLoading = false;

                        } else {
                            vm.$message({
                                showClose: true,
                                message: '导入失败！',
                                type: 'error'
                            });
                            vm.division.importDataLoading = false;
                        }
                    },
                    error: function(){
                        vm.$message({
                            showClose: true,
                            message: '导入失败！',
                            type: 'error'
                        });
                        vm.division.importDataLoading = false;
                    }
                });
            });
        },

        /**
         * 获取地图服务图层字段
         * @param url
         * @param layerType
         */
        getLayerInfos: function (url,layerName,layerType) {
            var vm = this;
            vm.division.importForm[vm.division.importType + 'Name'] = layerName;
            $.ajax({
                type: "get",
                url: ctx + "/agsupport/mapService/getFieldsByLayerType",
                data: {url: url, layerType: layerType},
                async: false,
                dataType: "json",
                success: function (result) {
                    if (result.success) {
                        Vue.set(vm.division,vm.division.importType + 'LayerFields',result.fields);
                    }
                },
                error: function () {
                    //请求出错处理
                }
            });

        },

        /**
         * 导入图层dialog关闭事件
         */
        layerImportDialogClosed: function () {
            Vue.set(this.division,'cityLayerFields',[]);
            this.division.citySelectedFieldValue = '';
            this.division.citySelectedNameValue = '';
            Vue.set(this.division,'streetLayerFields',[]);
            this.division.streetSelectedFieldValue = '';
            this.division.streetSelectedNameValue = '';
            Vue.set(this.division,'countryLayerFields',[]);
            this.division.countrySelectedFieldValue = '';
            this.division.countrySelectedNameValue = '';
            Vue.set(this.division,'importForm',{
                cityUrl: '',
                cityName: '',
                streetUrl: '',
                streetName: '',
                countryUrl: '',
                countryName:''
            });
        },

        /**
         * 打开地图资源dialog
         * @param type
         */
        openLayerResourceDialog: function (type) {
            this.division.importType = type;
            this.division.loadLayerResourceDialogVisible = true;
            this.division.resourceLayersCurrentPage = 1;
            this.division.resourceLayersRows = 10;
            this.division.resourceLayersSearch = '';
            this.loadResourceLayers();
        },

        /**
         * 加载图层数据资源
         */
        loadResourceLayers: function () {
            var vm = this;
            var param = {
                page: vm.division.resourceLayersCurrentPage,
                rows: vm.division.resourceLayersRows,
                name: vm.division.resourceLayersSearch
            };
            $.ajax({
                url: ctx + '/agsupport/dir/layerList',
                type: 'get',
                dataType:'json',
                data: param,
                async: true, type: 'get',
                dataType:'json',
                data: param,
                async: true,
                success: function (result) {
                    var layers = result.content.rows;
                    var total = result.content.total;
                    Vue.set(vm.division,'resourceLayers',layers);
                    if(vm.division.resourceLayersTotal === null) {
                        vm.division.resourceLayersTotal = total;
                    }
                    vm.division.loadLayerResourceLoading = false;
                },
                error: function (err) {
                    vm.division.loadLayerResourceLoading = false;
                }
            })
        },

        /**
         * 导入图层
         */
        improtResourceLayer: function () {
            var sels = this.$refs.resourceLayerTable.selection;
            if(sels.length !== 1) {
                this.$alert('请选择一个图层！', '系统提示', {
                    confirmButtonText: '确定'
                });
                return;
            }
            var url = sels[0].url;
            var layerTable = sels[0].layerTable;
            var layerType = sels[0].layerType;
            this.division.currentLayerType = layerType;
            var layerName = sels[0].nameCn;
            var layerUrl = '';
            if(layerType === '020202') { //ArcGIS MapServer(Dynamic)
                layerUrl = url + '/' + layerTable;
            }else if(layerType ==='040002') { //ArcGIS WFS
                layerUrl = url + '?' + "typeName=" + layerTable;
            }else if(layerType === '040003') { //GeoServer WFS
                layerUrl = url + '?' + "typeName=" + layerTable;
            }else if(layerType === '030003') { //GeoServer WMS
                layerUrl = url + '?' + "typeName=" + layerTable;
            }

            this.division.importForm[this.division.importType + 'Url'] = layerUrl;
            this.getLayerInfos(layerUrl,layerName,layerType);
            this.division.loadLayerResourceDialogVisible = false;
        },

        selectResourceLayerCurrentRow: function (row) {
            row.selected = !row.selected;
            this.$refs.resourceLayerTable.toggleRowSelection(row,row.selected);
        },


        selectCurrentRow: function (row) {
            row.selected = !row.selected;
            this.$refs.divisionLayerTable.toggleRowSelection(row,row.selected);
        },

        selectAll: function (selection) {
            selection.forEach(function(row){
                row.selected = !row.selected;
            });
        },
    },
    computed: {
        layerSearcher: function(){
            var searchKeys = this.division.layerSearch.toLowerCase();
            return this.division.layers.filter(function (layer) {
                return layer.name.toLowerCase().indexOf(searchKeys) > -1
                    || layer.code.toLowerCase().indexOf(searchKeys) > -1
            });
        }
    },
    mounted: function () {
        this.loadLayers();
    }
});