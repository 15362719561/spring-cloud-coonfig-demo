var app = new Vue({
    el: '#app',
    data: {
        ctx: ctx,
        conflictCheck: {
            layers: [], //冲突检测图层
            layerTypes: [],
            searchLayers: [], //查询结果图层
            layerLandTypes: [], //图层用地性质
            landTypesSelectIds: [], //非建设用地ids
            layerSearch: '', //查询关键字
            editLayerForm: {}, //编辑界面图层表单信息
            selectedCounnt: 0, //选中的行
            emptyLayerForm: {  //空表单信息
                layerType: '',
                layerName: '',
                layerAliasName: '',
                layerUrl: '',
                nameField: '',
                typeField:''
            },
            addLayerForm: {  //新增界面图层表单信息
                layerType: '',
                layerName: '',
                layerAliasName: '',
                layerUrl: '',
                nameField: '',
                typeField:'',
                seq: 0,
            },
            addLandTypeForm: {
                id: null,
                landName: '',
                landCode: '',
                layerType: '',
                construct: 1,
            },
            editLandTypeForm: {
                id: null,
                landName: '',
                landCode: '',
                layerType: '',
                construct: 1,
            },
            emptyLandTypeForm: {
                id: null,
                landName: '',
                landCode: '',
                layerType: '',
                construct: 1,
            },
            jsydLandTypeForms: [

            ],
            fjsydLandTypeForms: [

            ],
            currentLayerId: '',
            layerFormRules: {
                layerName: [
                    { required: true, message: '请输入图层名称', trigger: 'blur' }
                ],
                layerUrl: [
                    { required: true, message: '请输入服务地址', trigger: 'blur' }
                ],
                typeField: [
                    { required: true, message: '请选择编码字段', trigger: 'blur' }
                ],
                layerType: [
                    { required: true, message: '请选择图层类型', trigger: 'blur' }
                ]
            },
            landTypeFormRules: {
                landName: [
                    { required: true, message: '请输入用地性质名称', trigger: 'blur' }
                ],
                landCode: [
                    { required: true, message: '请输入用地性质编码', trigger: 'blur' }
                ]
            },
            editLoading: true,
            layerFields: [],
            addLayerFields: [],
            layerNameFieldsSelectValue:'',
            layerTypeFieldsSelectValue:'',
            layerTypeSelectValue: '',
            currentOperateConstruct:'',
            landTypesViewVisible: false,
            conflictCheckLayerViewVisible: true,
            editLayerViewVisible: false,
            addLayerViewVisible: false,
            addLandTypeViewVisible: false,
            editLandTypeViewVisible: false,
            importLandTypeViewVisible: false,
            layersTableLoading: false,
            landTypesTransferLoading: false,
            loadLayerResourceLoading: false,
            loadLayerResourceDialogVisible: false,
            resourceLayers: [],
            resourceLayersCurrentPage: 1,
            resourceLayersRows: 10,
            resourceLayersSearch: '',
            resourceLayersTotal: null,

            landTypesTransferHeight: window.document.body.clientHeight - 90,
            layersTableHeight: window.document.body.clientHeight - 150,
            fileList:[],
            uploadData:{},
            rows: 10,
            total: null,
            currentPage: 1
        }
    },
    methods: {
        /**
         * 获取冲突检测配置图层
         */
        getConflictCheckLayers: function(){
            var vm = this;
            vm.conflictCheck.layersTableLoading = true;
            var param = {
                page: vm.conflictCheck.currentPage,
                rows: vm.conflictCheck.rows,
                layerName: vm.conflictCheck.layerSearch
            };
            $.ajax({
                url:  ctx + '/agsupport/conflictCheck/findConflictCheckLayerList',
                type: 'get',
                data: param,
                async: true,
                success: function(result){
                    Vue.set(vm.conflictCheck,'layers',result.rows);
                    if(vm.conflictCheck.total === null) {
                        vm.conflictCheck.total =result.total;
                    }
                    vm.conflictCheck.layersTableLoading = false;
                },
                error: function () {
                    vm.conflictCheck.layersTableLoading = false;
                }
            });
        },

        /**
         * 获取图层用地性质配置
         * @param layerId
         */
        getLayerLandTypes: function(layerId){
            var vm = this;
            vm.conflictCheck.landTypesTransferLoading = true;
            $.ajax({
                url: ctx + '/agsupport/conflictCheck/findConflictCheckLandType',
                type: 'get',
                async: true,
                data: {rows: 100000,layerType: layerId},
                success: function (result) {
                    result = typeof result === 'string' ? JSON.parse(result).rows : result.rows;
                    Vue.set(vm.conflictCheck,'layerLandTypes',result);
                    var landTypesSelectIds = [];
                    for(var i = 0; i < result.length; i++) {
                        if(result[i].construct === '1') continue;
                        landTypesSelectIds.push(result[i].id);
                    }
                    Vue.set(vm.conflictCheck,'landTypesSelectIds',landTypesSelectIds);
                    vm.conflictCheck.landTypesTransferLoading = false;
                },
                error: function(){
                    vm.conflictCheck.landTypesTransferLoading = false;
                }
            });
        },

        /**
         * 打开图层用地性质配置界面
         * @param row
         */
        showLayerLandTypesView: function(row){
            this.getLayerLandTypes(row.id);
            this.conflictCheck.landTypesViewVisible = true;
            this.conflictCheck.conflictCheckLayerViewVisible = false;
            var landTypeForm = Object.assign({},this.conflictCheck.emptyLandTypeForm);
            landTypeForm.layerType = row.id;
            Vue.set(this.conflictCheck,'addLandTypeForm',landTypeForm);
            this.conflictCheck.currentLayerId = row.id;
        },

        /**
         * 打开图层配置界面
         */
        showConflictCheckLayerView: function(){
            this.conflictCheck.conflictCheckLayerViewVisible = true;
            this.conflictCheck.landTypesViewVisible = false;
        },

        /**
         * 打开新增图层界面
         * @param row
         */
        showAddLayerView: function (row) {
            this.conflictCheck.addLayerViewVisible = true;
            this.conflictCheck.layerTypeFieldsSelectValue = '';
            this.conflictCheck.layerNameFieldsSelectValue = '';
            this.conflictCheck.layerTypeSelectValue = '';
            Vue.set(this.conflictCheck,'addLayerFields',[]);
            var emptyForm = Object.assign({},this.conflictCheck.emptyLayerForm);
            Vue.set(this.conflictCheck,'addLayerForm',emptyForm);
            this.resetForm('addLayerForm');
            this.conflictCheck.addLayerForm.seq = this.conflictCheck.layers.length + 1;
        },

        /**
         * 打开excel导入图层界面
         * @param construct
         */
        showImportLandTypeView: function (construct) {
            this.conflictCheck.importLandTypeViewVisible = true;
            this.conflictCheck.currentOperateConstruct = construct;
        },

        /**
         * 新增图层
         */
        addLayer: function () {
            var vm = this;
            if(!vm.validateFrom('addLayerForm')) return;
            $.ajax({
                url: ctx + '/agsupport/conflictCheck/saveConflictCheckLayer',
                type: 'post',
                data: vm.conflictCheck.addLayerForm,
                async: false,
                success: function (data) {
                    vm.$message({
                        showClose: true,
                        message: '保存成功！',
                        type: 'success'
                    });
                    vm.conflictCheck.addLayerViewVisible = false;
                    vm.conflictCheck.rows = 10;
                    vm.conflictCheck.total = null;
                    vm.conflictCheck.currentPage = 1;
                    vm.getConflictCheckLayers();
                },
                error: function () {

                }
            });
        },

        /**
         * 删除图层
         * @param row
         */
        deleteLayer: function (row) {
            var vm = this;
            this.$confirm('此操作将永久删除当前图层, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(function(){
                $.post(ctx + '/agsupport/conflictCheck/deleteConflictCheckLayer', {paramIds: row.id}, function (result) {
                    if (result.success) {
                        vm.$message({
                            showClose: true,
                            message: '删除成功！',
                            type: 'success'
                        });
                        vm.conflictCheck.rows = 10;
                        vm.conflictCheck.total = null;
                        vm.conflictCheck.currentPage = 1;
                        vm.getConflictCheckLayers();
                    }
                }, 'json');
            });
        },

        /**
         * 批量删除
         */
        deleteSelectedLayers: function(){
            var selection = this.$refs.conflictCheckLayerTable.selection;
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
                $.post(ctx + '/agsupport/conflictCheck/deleteConflictCheckLayer', {paramIds: ids.join(',')}, function (result) {
                    if (result.success) {
                        vm.$message({
                            showClose: true,
                            message: '删除成功！',
                            type: 'success'
                        });
                        vm.conflictCheck.rows = 10;
                        vm.conflictCheck.total = null;
                        vm.conflictCheck.currentPage = 1;
                        vm.getConflictCheckLayers();
                    }
                }, 'json');
            });

        },

        /**
         * 打开图层编辑界面
         * @param row
         */
        showEditLayerView: function(row){
            var fields = this.getLayerFielsByType(row.layerUrl,row.layerType);
            Vue.set(this.conflictCheck, 'layerFields',fields);
            this.conflictCheck.editLayerViewVisible = true;
            var editRow = Object.assign({},row);
            Vue.set(this.conflictCheck,'editLayerForm',editRow);
            this.conflictCheck.layerNameFieldsSelectValue = editRow.nameField;
            this.conflictCheck.layerTypeFieldsSelectValue = editRow.typeField;
            this.conflictCheck.layerTypeSelectValue = editRow.layerType;
            this.resetForm('editLayerForm');
        },

        selectCurrentRow: function (row) {
            row.selected = !row.selected;
            this.$refs.conflictCheckLayerTable.toggleRowSelection(row,row.selected);
        },

        selectAll: function (selection) {
            selection.forEach(function(row){
                row.selected = !row.selected;
            });
        },

        /**
         * 保存编辑图层
         */
        saveEditLayer: function(){
            var vm = this;
            if(!vm.validateFrom('editLayerForm')) return;
            $.ajax({
                url: ctx + '/agsupport/conflictCheck/saveConflictCheckLayer',
                type: 'post',
                data: vm.conflictCheck.editLayerForm,
                async: false,
                success: function (data) {
                    vm.$message({
                        showClose: true,
                        message: '保存成功！',
                        type: 'success'
                    });
                    vm.conflictCheck.editLayerViewVisible = false;
                    vm.getConflictCheckLayers();
                },
                error: function () {
                    vm.$message({
                        showClose: true,
                        message: '保存失败！',
                        type: 'error'
                    });
                }
            });
        },

        /**
         * 打开添加用地性质界面
         * @param construct
         */
        showAddLandTypeView: function (construct) {
            this.conflictCheck.addLandTypeViewVisible = true;
            this.conflictCheck.currentOperateConstruct = construct;
            var landTypeForm = Object.assign({},this.conflictCheck.emptyLandTypeForm);
            landTypeForm.construct = this.conflictCheck.currentOperateConstruct;
            landTypeForm.layerType = this.conflictCheck.addLandTypeForm.layerType;
            Vue.set(this.conflictCheck,'addLandTypeForm',landTypeForm);
        },

        /**
         * 打开用地性质编辑界面
         * @param construct
         */
        showEditLandTypeView: function (construct) {
            this.conflictCheck.currentOperateConstruct = construct;
            if(construct === 1) {
                if(this.conflictCheck.jsydLandTypeForms.length !== 1) {
                    this.$alert('请选择一条数据', '系统提示', {
                        confirmButtonText: '确定'
                    });
                    return;
                }else {
                    var selectedForm = Object.assign({},this.conflictCheck.jsydLandTypeForms[0]);
                    Vue.set(this.conflictCheck,'editLandTypeForm',selectedForm);

                }
            }else if(construct === 0) {
                if(this.conflictCheck.jsydLandTypeForms.length !== 1) {
                    this.$alert('请选择一条数据', '系统提示', {
                        confirmButtonText: '确定'
                    });
                    return;
                }else {
                    var selectedForm = Object.assign({},this.conflictCheck.fjsydLandTypeForms[0]);
                    Vue.set(this.conflictCheck,'editLandTypeForm',selectedForm);
                }
            }
            this.conflictCheck.editLandTypeViewVisible = true;
            this.resetForm('editLandTypeForm');
        },

        /**
         * 添加用地性质
         */
        addLandType: function () {
            var vm = this;
            if(! vm.validateFrom('addLandTypeForm')) return;
            $.ajax({
                url: ctx + '/agsupport/conflictCheck/saveConflictCheckLandType',
                type: 'post',
                data: vm.conflictCheck.addLandTypeForm,
                async: false,
                success: function(result){
                    if(typeof result === 'string') result = JSON.parse(result);
                    if(result.success) {
                        vm.conflictCheck.addLandTypeViewVisible = false;
                        vm.$message({
                            showClose: true,
                            message: '保存成功！',
                            type: 'success'
                        });
                        vm.getLayerLandTypes(vm.conflictCheck.addLandTypeForm.layerType);
                    }
                },
                error: function () {
                    vm.$message({
                        showClose: true,
                        message: '保存失败！',
                        type: 'error'
                    });
                    vm.conflictCheck.addLandTypeViewVisible = false;
                }
            });
        },

        /**
         * 编辑用地性质
         */
        editLandType: function () {
            var vm = this;
            if(! vm.validateFrom('editLandTypeForm')) return;
            $.ajax({
                url: ctx + '/agsupport/conflictCheck/saveConflictCheckLandType',
                type: 'post',
                data: vm.conflictCheck.editLandTypeForm,
                async: false,
                success: function(result){
                    if(typeof result === 'string') result = JSON.parse(result);
                    if(result.success) {
                        vm.conflictCheck.editLandTypeViewVisible = false;
                        vm.$message({
                            showClose: true,
                            message: '保存成功！',
                            type: 'success'
                        });
                        vm.getLayerLandTypes(vm.conflictCheck.editLandTypeForm.layerType);
                    }
                },
                error: function () {
                    vm.$message({
                        showClose: true,
                        message: '保存失败！',
                        type: 'error'
                    });
                    vm.conflictCheck.editLandTypeViewVisible = false;
                }
            });
        },

        /**
         * 删除用地性质
         * @param type
         */
        deleteLandTypes: function(type){
            var deleteIds = [],vm = this;
            this.$confirm('此操作将永久当前删选中的用地性质, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(function(){
                if(type === 0) { //非建设用地
                    vm.conflictCheck.fjsydLandTypeForms.forEach(function(item,index){
                        deleteIds.push(item.id);
                    });
                    Vue.set(vm.conflictCheck,'fjsydLandTypeForms',[]);
                }else if(type === 1) { //建设用地
                    vm.conflictCheck.jsydLandTypeForms.forEach(function(item,index){
                        deleteIds.push(item.id);
                    });
                    Vue.set(vm.conflictCheck,'jsydLandTypeForms',[]);
                }
                $.ajax({
                    url: ctx + '/agsupport/conflictCheck/deleteBatchConflictCheckLandType',
                    type: 'post',
                    data: {paramIds: deleteIds.join(",")},
                    async: false,
                    success: function(result){
                        if(typeof result === 'string') result = JSON.parse(result);
                        if(result.success) {
                            vm.$message({
                                showClose: true,
                                message: '删除成功！',
                                type: 'success'
                            });
                            vm.getLayerLandTypes(vm.conflictCheck.addLandTypeForm.layerType);
                        }else {
                            vm.$message({
                                showClose: true,
                                message: '删除失败！',
                                type: 'error'
                            });
                        }
                    },
                    error: function () {
                        vm.$message({
                            showClose: true,
                            message: '删除失败！',
                            type: 'error'
                        });
                    }
                });
            });
        },

        /**
         * 用地性质切换
         * @param value
         * @param direction
         * @param ids
         */
        transferChange: function(value,direction,ids){
            var vm = this,reauestData;
            if(direction === 'left') { //非建设用地转建设用地
                Vue.set(this.conflictCheck,'jsydLandTypeForms',[]);
                ids.forEach(function(item,index){
                    var landType = vm.conflictCheck.layerLandTypes.filter(function(d){
                        return  d.id === item;
                    })[0];
                    landType.construct = 1;
                    vm.conflictCheck.jsydLandTypeForms.push(landType);
                });
                reauestData = JSON.stringify(vm.conflictCheck.jsydLandTypeForms);
            }else if(direction === 'right') { //建设用地转非建设用地
                Vue.set(this.conflictCheck,'fjsydLandTypeForms',[]);
                ids.forEach(function(item,index){
                    var landType = vm.conflictCheck.layerLandTypes.filter(function(d){
                        return  d.id === item;
                    })[0];
                    landType.construct = 0;
                    vm.conflictCheck.fjsydLandTypeForms.push(landType);
                });
                reauestData = JSON.stringify(vm.conflictCheck.fjsydLandTypeForms);
            }
            $.ajax({
                url: ctx + '/agsupport/conflictCheck/updateBatchConflictCheckLandType',
                type: 'post',
                data: {listStr: reauestData},
                async: false,
                success: function(result){
                    if(typeof result === 'string') result = JSON.parse(result);
                    if(result.success) {
                        vm.$message({
                            showClose: true,
                            message: '保存成功！',
                            type: 'success'
                        });
                    }else {
                        vm.$message({
                            showClose: true,
                            message: '保存失败！',
                            type: 'error'
                        });
                        vm.showLayerLandTypesView();
                    }
                },
                error: function () {

                }
            });
        },

        /**
         * 建设用地数据
         * @param ids
         */
        transferLeftCheckChange: function (ids) {
            var vm = this;
            Vue.set(this.conflictCheck,'jsydLandTypeForms',[]);
            ids.forEach(function(item,index){
                var landType = vm.conflictCheck.layerLandTypes.filter(function(d){
                    return  d.id === item;
                })[0];
                landType.construct = 1;
                vm.conflictCheck.jsydLandTypeForms.push(landType);
            });
        },

        /**
         * 非建设用地数据
         * @param ids
         */
        transferRightCheckChange: function (ids) {
            var vm = this;
            Vue.set(this.conflictCheck,'fjsydLandTypeForms',[]);
            ids.forEach(function(item,index){
                var landType = vm.conflictCheck.layerLandTypes.filter(function(d){
                    return  d.id === item;
                })[0];
                landType.construct = 0;
                vm.conflictCheck.fjsydLandTypeForms.push(landType);
            });
        },

        /**
         * 分页切换
         * @param page
         */
        handleLayersPageChange: function (page) {
            this.conflictCheck.currentPage = page;
            this.getConflictCheckLayers();
        },

        selsChange: function(sels){
            this.conflictCheck.selectedCounnt = sels.length;
        },

        /**
         * 搜索
         */
        searchKeyChange: function () {
            this.conflictCheck.rows = 10;
            this.conflictCheck.total = null;
            this.conflictCheck.currentPage = 1;
            this.getConflictCheckLayers();
        },

        /**
         * 清空搜索
         */
        clearSearch: function () {
            this.conflictCheck.layerSearch = '';
            this.conflictCheck.rows = 10;
            this.conflictCheck.total = null;
            this.conflictCheck.currentPage = 1;
            this.getConflictCheckLayers();
        },

        /**
         * 打开地图资源dialog
         * @param type
         */
        openLayerResourceDialog: function () {
            this.conflictCheck.loadLayerResourceDialogVisible = true;
            this.conflictCheck.resourceLayersCurrentPage = 1;
            this.conflictCheck.resourceLayersRows = 10;
            this.conflictCheck.resourceLayersSearch = '';
            this.loadResourceLayers();
        },

        /**
         * 加载图层数据资源
         */
        loadResourceLayers: function () {
            var vm = this;
            vm.conflictCheck.loadLayerResourceLoading = true;
            var param = {
                page: vm.conflictCheck.resourceLayersCurrentPage,
                rows: vm.conflictCheck.resourceLayersRows,
                name: vm.conflictCheck.resourceLayersSearch
            };
            $.ajax({
                url: ctx + '/agsupport/dir/layerList',
                type: 'get',
                dataType:'json',
                data: param,
                async: true,
                type: 'get',
                dataType:'json',
                data: param,
                async: true,
                success: function (result) {
                    var layers = result.content.rows;
                    var total = result.content.total;
                    Vue.set(vm.conflictCheck,'resourceLayers',layers);
                    if(vm.conflictCheck.resourceLayersTotal === null) {
                        vm.conflictCheck.resourceLayersTotal = total;
                    }
                    vm.conflictCheck.loadLayerResourceLoading = false;
                },
                error: function (err) {
                    vm.conflictCheck.loadLayerResourceLoading = false;
                }
            })
        },

        /**
         * 切换地图资源分页
         * @param page
         */
        handleResourceLayersPageChange: function (page) {
            this.conflictCheck.resourceLayersCurrentPage = page;
            this.loadResourceLayers();
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
            var layerType = sels[0].layerType;
            var layerTable =  sels[0].layerTable;
            this.conflictCheck.loadLayerResourceDialogVisible = false;
            this.showAddLayerView();
            this.conflictCheck.addLayerForm.layerAliasName = sels[0].nameCn;
            this.conflictCheck.addLayerForm.layerName = sels[0].name;
            this.conflictCheck.addLayerForm.layerType = layerType;
            this.conflictCheck.layerTypeSelectValue = layerType;
            if(layerType === '020202') { //ArcGIS MapServer(Dynamic)
                this.conflictCheck.addLayerForm.layerUrl = url + '/' + layerTable;
            }else if(layerType ==='040002') { //ArcGIS WFS
                this.conflictCheck.addLayerForm.layerUrl = url + '?' + "typeName=" + layerTable;
            }else if(layerType === '040003') { //GeoServer WFS
                this.conflictCheck.addLayerForm.layerUrl = url + '?' + "typeName=" + layerTable;
            }else if(layerType === '030003') { //GeoServer WMS
                this.conflictCheck.addLayerForm.layerUrl = url + '?' + "typeName=" + layerTable;
            }
            //this.layerUrlChange(this.conflictCheck.addLayerForm.layerUrl,layerType);
            this.resetLayerFields();

        },

        /**
         * 地图图层关键字查询
         */
        resourceLayerSearchKeyChange: function () {
            this.conflictCheck.resourceLayersRows = 10;
            this.conflictCheck.resourceLayersTotal = null;
            this.conflictCheck.resourceLayersCurrentPage = 1;
            this.loadResourceLayers();
        },

        /**
         * 清空地图图层关键字查询
         */
        clearResourceLayerSearch: function () {
            this.conflictCheck.resourceLayersSearch = '';
            this.conflictCheck.resourceLayersRows = 10;
            this.conflictCheck.resourceLayersTotal = null;
            this.conflictCheck.resourceLayersCurrentPage = 1;
            this.loadResourceLayers();
        },

        getLayerTypes: function () {
           var vm = this;
            $.ajax({
                url: ctx + '/agsupport/dic/getAgDicByTypeCode/A010',
                type: 'get',
                async: false,
                dataType: "json",
                success: function (result) {
                    if(result.success){
                        Vue.set(vm.conflictCheck,'layerTypes',result.content);
                    }
                },
                error: function (e) {

                }
            });
        },

        /**
         * 获取地图服务字段
         * @param url
         * @returns {Array}
         */
        getLayerFielsByType: function (url,layerType) {
            var vm = this;
            var fields = [];
            var resultCall = function(data){
                var result = JSON.parse(data);
                if (result.success == true) {
                    for (var i = 0; i < result.fields.length; i++) {
                        var field = result.fields[i];
                        if (field.name.toLowerCase().indexOf("shape") > -1) {
                            continue;
                        }
                        fields.push(field);
                    }
                    fields.push({"name": "area", alias: "面积"});//增加面积字段
                }
            }
            if (url != "") {
                $.ajax({
                    url: ctx + '/agsupport/mapService/getFieldsByLayerType',
                    type: 'post',
                    data: { url: url,layerType: layerType},
                    async: false,
                    success: resultCall,
                    error: function () {
                        this.$message({
                            showClose: true,
                            message: '获取数据出错！',
                            type: 'error'
                        });
                    }
                });
            }
            return fields;
        },

        /**
         * 根据图层类型编码获取图层类型名称
         * @param row
         * @param column
         */
        layerTypeFormatter: function (row, column) {
            var layerTypeObj = this.conflictCheck.layerTypes.filter(function(item){
                return item.code === row.layerType;
            })[0];
            return layerTypeObj.name;
        },

        /**
         * 上传excel
         */
        submitUpload: function(){
            var layerType = this.conflictCheck.currentLayerId;
            var construct = this.conflictCheck.currentOperateConstruct;
            Vue.set(this.conflictCheck,'uploadData',{
                layerType: layerType,
                construct: construct
            });
            setTimeout(function(){
                app.$refs["uploadExcel"].submit();
            },200);
        },

        /**
         * 取消上传
         */
        abortUpload: function(){
            this.conflictCheck.fileList.every(function (file) {
                return app.$refs["uploadExcel"].abort(file);
            });
            this.clearUpload();
        },

        /**
         * 清空上传列表
         */
        clearUpload: function () {
            this.$refs["uploadExcel"].clearFiles()
        },

        /**
         * 上传限制
         */
        uploadLimit: function () {
            this.$message({
                showClose: true,
                message: '最多上传一个文件！',
                type: 'error'
            });
        },

        uploadBefore: function (file) {

        },

        /**
         * excel上传成功
         * @param response
         * @param file
         * @param fileList
         */
        uploadSuccess: function(response, file, fileList) {
            if(response.success) {
                this.$message({
                    showClose: true,
                    message: '文件上传成功！',
                    type: 'success'
                });
                this.conflictCheck.importLandTypeViewVisible = false;
                this.getLayerLandTypes(this.conflictCheck.currentLayerId);
            }else {
                this.$message({
                    showClose: true,
                    message: response.message,
                    type: 'error'
                });
            }

        },

        layerTypeSelectChange: function (value) {
            if(this.conflictCheck.editLayerViewVisible) { //编辑状态
                var form = Object.assign({},this.conflictCheck.editLayerForm);
                form.layerType = value;
                Vue.set(this.conflictCheck,'editLayerForm',form);
            }else {//新增状态
                var form = Object.assign({},this.conflictCheck.addLayerForm);
                form.layerType = value;
                Vue.set(this.conflictCheck,'addLayerForm',form);
            }
        },

        layerNameFieldsSelectChange: function (value) {
            if(this.conflictCheck.editLayerViewVisible) { //编辑状态
                var form = Object.assign({},this.conflictCheck.editLayerForm);
                form.nameField = value;
                Vue.set(this.conflictCheck,'editLayerForm',form);
            }else {//新增状态
                var form = Object.assign({},this.conflictCheck.addLayerForm);
                form.nameField = value;
                Vue.set(this.conflictCheck,'addLayerForm',form);
            }

        },

        layerTypeFieldsSelectChange: function (value) {
            if(this.conflictCheck.editLayerViewVisible) { //编辑状态
                var form = Object.assign({},this.conflictCheck.editLayerForm);
                form.typeField = value;
                Vue.set(this.conflictCheck,'editLayerForm',form);
            }else {//新增状态
                var form = Object.assign({},this.conflictCheck.addLayerForm);
                form.typeField = value;
                Vue.set(this.conflictCheck,'addLayerForm',form);
            }
        },

        layerUrlChange: function (url,layerType) {
            var fields = this.getLayerFielsByType(url,layerType);
            if(this.conflictCheck.editLayerViewVisible) { //编辑状态
                Vue.set(this.conflictCheck, 'layerFields',fields);
            }else { //新增状态
                Vue.set(this.conflictCheck, 'addLayerFields',fields);
            }
        },

        resetLayerFields: function () {
            if(this.conflictCheck.editLayerViewVisible) { //编辑状态
                var fields = this.getLayerFielsByType(this.conflictCheck.editLayerForm.layerUrl,this.conflictCheck.editLayerForm.layerType);
                Vue.set(this.conflictCheck, 'layerFields',fields);
            }else { //新增状态
                var fields = this.getLayerFielsByType(this.conflictCheck.addLayerForm.layerUrl,this.conflictCheck.addLayerForm.layerType);
                Vue.set(this.conflictCheck, 'addLayerFields',fields);
            }
        },

        validateFrom: function(formName){
            var _valid = true;
            this.$refs[formName].validate(function(valid){
                if (!valid) {
                    _valid = false
                }
            });
            return _valid;
        },
        resetForm: function(formName){
            if(this.$refs[formName]) {
                this.$refs[formName].resetFields();
            }
        }
    },
    computed: {
        layerSearcher: function(){
            var searchKeys = this.conflictCheck.layerSearch.toLowerCase();
            return this.conflictCheck.layers.filter(function (layer) {
                return layer.layerName.toLowerCase().indexOf(searchKeys) > -1
                    || layer.layerAliasName.toLowerCase().indexOf(searchKeys) > -1
            });
        }
    },
    mounted: function(){
        var vm = this;
        this.getLayerTypes();
        this.getConflictCheckLayers();
        this.conflictCheck.landTypesTransferHeight = window.document.body.clientHeight - 90;
        this.conflictCheck.layersTableHeight = window.document.body.clientHeight - 150;
        window.onresize = function(){
            vm.conflictCheck.landTypesTransferHeight = window.document.body.clientHeight - 90;
            vm.conflictCheck.layersTableHeight = window.document.body.clientHeight - 150;
        }
    }
});