var app = new Vue({
    el: '#app',
    data: {
        complianceCheck: {
            layers: [],
            layerTypes: [],
            titleVisible: false,
            toolVisible: true,
            layerViewVisible: true,
            multipleLayerSelection: [],
            layerNameSearch: '',
            emptyLayerForm: {  //空表单信息
                layerName: '',//图层名称
                layerWhere: '',//图层条件
                seq: 0,//排序序号
                showField: '',//前端显示字段
                typeField: '',//类型判断字段
                regulation: '1',//规则
                screenshot: '0',//是否截图1是0否
                status: '1',//是否启用1是0否
                remark: '',//图层标识
                layerUrl: '',//图层服务地址
                layerType: '',//图层分类
                source: '-1'//数据来源:-1手动增加，串ID表示从图层目录关联供级联删除
            },
            //图层标识
            remarks: [
                {value: "LayerName_TG", label: "土规"},
                {value: "LayerName_KG", label: "控规"},
                {value: "LayerName_STKJ", label: "生态空间"},
                {value: "LayerName_CZKJ", label: "城镇空间"},
                {value: "LayerName_NYKJ", label: "农业空间"},
                {value: "LayerName_STHX", label: "生态保护红线"},
                {value: "LayerNAME_CZKFBJ", label: "城市增长边界"},
                {value: "LayerName_JBNT", label: "基本农田保护红线"}
            ],
            layerFields: [],
            booleanDatas: [],
            editLayerProjectViewVisible: false,
            projectDatas: [],
            projectSelectValue: '',
            projectLayers: [],
            currRow: {},
            treeData: [],
            currTreeData: [],
            currTreechecked: [],
            rangeCodes: [],
            rangeNames: [],
            treeProps: {
                label: 'name',
                children: 'children'
            }
        },
        layerDialogVisible: false,
        //图层关键字
        layerKeyword: "",
        //图层列表
        layerTableData: [],
        layerLoading: false,
        //分页
        layerTablePageSize: 10,
        layerTableCurrentPage: 1,
        layerTableTotal: 0,
        //被选图层
        multipleLayerSelection: []
    },
    methods: {

        getLayerTypes: function () {
            var vm = this;
            $.ajax({
                url: ctx + '/agsupport/dic/getAgDicByTypeCode/A010',
                type: 'get',
                async: false,
                dataType: "json",
                success: function (result) {
                    if(result.success){
                        Vue.set(vm.complianceCheck,'layerTypes',result.content);
                    }
                },
                error: function (e) {

                }
            });
        },

        getComplianceCheckLayers: function () {
            var vm = this;
            $.ajax({
                url: ctx + '/agsupport/complianceCheck/list',
                type: 'post',
                data: {layerName: vm.complianceCheck.layerNameSearch},
                async: true,
                dataType: "json",
                success: function (result) {
                    if (result.success) {
                        result = typeof result.message === 'string' ? JSON.parse(result.message) : result.message;
                        Vue.set(vm.complianceCheck, 'layers', result);
                    }
                },
                error: function () {

                }
            });
        },
        onTableRowSelectionChange: function (selection) {
            this.complianceCheck.multipleLayerSelection = selection;
        },
        //获取服务字段
        getLayerFielsByType: function (url,layerType) {
            var vm = this;
            var fields = [];
            if (url != "") {
                $.ajax({
                    url: ctx + '/agsupport/mapService/getFieldsByLayerType',
                    type: 'post',
                    data: {url: url,layerType: layerType},
                    async: false,
                    success: function (data) {
                        var result = JSON.parse(data);
                        if (result.success) {
                            for (var i = 0; i < result.fields.length; i++) {
                                var field = result.fields[i];
                                if (field.name.toLowerCase().indexOf("shape") > -1) {
                                    continue;
                                }
                                fields.push(field);
                            }

                        }
                    },
                    error: function () {
                        this.$message({
                            showClose: true,
                            message: '获取图层字段数据出错！',
                            type: 'error'
                        });
                    }
                });
            }
            return fields;
        },
        /**
         * 获取是否下拉框值
         * @returns {Array}
         */
        getBoolean: function () {
            var list = [];
            var vm = this;
            $.ajax({
                url: ctx + 'agsupport/dic/getAgDicByTypeCode/A006',
                type: 'get',
                async: true,
                dataType: "json",
                success: function (result) {
                    if (result.success) {
                        list = result.content == "" ? [] : result.content;
                    }
                    for (var i = 0; i < list.length; i++) {
                        if (list[i].value == 1) {
                            list[i].label = "允许相交";
                        } else if (list[i].value == 0) {
                            list[i].label = "不允许相交";
                        }
                    }
                    Vue.set(vm.complianceCheck, 'booleanDatas', list);
                },
                error: function (e) {
                }
            });
        },
        /**
         * 从专题数据里导入检测图层
         */
        importLayerView: function () {
            var vm = this;
            vm.layerDialogVisible = true;
            vm.layerKeyword = '';
            vm.layerTableCurrentPage = 1;
            vm.layerTablePageSize = 10;
            vm.layerShowTable();
        },
        layerShowTable: function () {
            var vm = this;
            vm.layerLoading = true;
            var p = {
                page: vm.layerTableCurrentPage,
                rows: vm.layerTablePageSize,
                name: vm.layerKeyword
            };
            $.ajax({
                url: ctx + 'agsupport/dir/layerList',
                type: 'get',
                data: p,
                dataType: "json",
                async: false,
                success: function (res) {
                    vm.layerLoading = false;
                    if (res.success) {
                        vm.layerTableData = res.content.rows;
                        vm.layerTableTotal = res.content.total;
                    } else {
                        vm.layerTableData = [];
                        vm.layerTableTotal = [];
                    }
                },
                error: function () {
                    vm.layerLoading = false;
                }
            });
        },
        layerHandleSelectionChange: function (val) {
            this.multipleLayerSelection = val;
        },
        layerHandleCurrentChange: function (val) {
            this.layerTableCurrentPage = val;
            this.layerShowTable();
        },
        layerHandleRowSelection: function (row) {
            this.$refs.layerRowSelection.toggleRowSelection(row);
        },
        layerHandleSizeChange: function (val) {
            this.layerTablePageSize = val;
            this.layerShowTable();
        },
        selLayers: function () {
            var vm = this;
            var layers = [];
            $.each(vm.multipleLayerSelection, function (index, layer) {
                var url = layer.url;
                var layerTable = layer.layerTable;
                var layerType = layer.layerType;
                var layerObj = {
                    layerName: layer.nameCn,
                    layerWhere: '1=1',
                    regulation: '1',
                    screenshot: '0',
                    status: '1',
                    layerType: layerType,
                    source: layer.id,
                }
                if(layerType === '020202') { //ArcGIS MapServer(Dynamic)
                    layerObj.layerUrl = url + '/' + layerTable;
                }else if(layerType ==='040002') { //ArcGIS WFS
                    layerObj.layerUrl = url + '?' + "typeName=" + layerTable;
                }else if(layerType === '040003') { //GeoServer WFS
                    layerObj.layerUrl = url + '?' + "typeName=" + layerTable;
                }else if(layerType === '030003') { //GeoServer WMS
                    layerObj.layerUrl = url + '?' + "typeName=" + layerTable;
                }
                layers.push(layerObj);
            });
            if (layers.length === 0)  return;
            $.ajax({
                url: ctx + '/agsupport/complianceCheck/saveLayers',
                type: 'post',
                data: {
                    layersStr: JSON.stringify(layers)
                },
                async: true,
                success: function (data) {
                    vm.$message({
                        showClose: true,
                        message: '导入成功！',
                        type: 'info'
                    });
                    vm.layerDialogVisible = false;
                    vm.getComplianceCheckLayers();
                },
                error: function () {
                    vm.$message({
                        showClose: true,
                        message: '导入出错！',
                        type: 'error'
                    });
                }
            });
        },
        addLayerView: function () {
            var vm = this;
            var data = Object.assign({}, vm.complianceCheck.emptyLayerForm);
            var seq = vm.complianceCheck.layers.length + 1;
            $.each(vm.complianceCheck.layers, function (index, layer) {
                if (layer.seq >= seq) {
                    seq = layer.seq + 1;
                }
            });
            data.seq = seq;
            vm.complianceCheck.layers.push(data);
        },
        setFieldsBylayerUrl: function (row) {
            var fields = this.getLayerFielsByType(row.layerUrl,row.layerType);
            Vue.set(this.complianceCheck, 'layerFields', fields);
        },
        /**
         * 项目与检测图层关联
         */
        layerProjectView: function () {
            var vm = this;
            vm.complianceCheck.editLayerProjectViewVisible = true;
            if (vm.complianceCheck.projectSelectValue != "") {
                vm.getProjectLayers();
            }
        },
        /**
         * 删除图层
         */
        deleteLayer: function () {
            var vm = this;
            var itemIds = [];
            $.each(vm.complianceCheck.multipleLayerSelection, function (index, val) {
                itemIds.push(val.id);
            });
            if (itemIds.length == 0) {
                return;
            }
            vm.$confirm('此操作将永久删除当前选中图层, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(function () {
                $.post(ctx + '/agsupport/complianceCheck/delete', {paramIds: itemIds.join(",")}, function (result) {
                    if (result.success) {
                        vm.$message({
                            showClose: true,
                            message: '删除成功！',
                            type: 'success'
                        });
                        vm.getComplianceCheckLayers();
                    }
                }, 'json');
            });
        },
        saveEditLayer: function () {
            var vm = this;
            var rows = vm.complianceCheck.layers;
            var checkResult = true;
            var message = "";
            for (var i = 0; i < rows.length; i++) {
                if(rows[i].layerType == "") {
                    checkResult = false;
                    message = '图层类型不能为空，请检查输入！';
                    break;
                }
                if (rows[i].layerName == "") {
                    checkResult = false;
                    message = '图层名称不能为空，请检查输入！';
                    break;
                } else if (rows[i].layerUrl == "") {
                    checkResult = false;
                    message = '图层服务地址不能为空，请检查输入！';
                    break;
                } else if (rows[i].typeField == "") {
                    checkResult = false;
                    message = '类型判断字段不能为空，请检查输入！';
                    break;
                }
            }
            if (!checkResult) {
                vm.$message({
                    showClose: true,
                    message: message,
                    type: 'info'
                });
                return;
            }
            $.ajax({
                url: ctx + '/agsupport/complianceCheck/saveOrUpdate',
                type: 'post',
                data: {listStr: JSON.stringify(rows)},
                async: false,
                dataType: "json",
                success: function (result) {
                    if (result.success) {
                        vm.$message({
                            showClose: true,
                            message: "保存/修改成功",
                            type: 'info'
                        });
                        vm.getComplianceCheckLayers();
                    } else {
                        vm.$message({
                            showClose: true,
                            message: "保存/修改出错：" + result.message,
                            type: 'error'
                        });
                    }
                },
                error: function () {
                    vm.$message({
                        showClose: true,
                        message: '保存/修改出错！',
                        type: 'error'
                    });
                }
            });
        },
        validateFrom: function (formName) {
            var _valid = true;
            this.$refs[formName].validate(function (valid) {
                if (!valid) {
                    _valid = false;
                }
            });
            return _valid;
        },
        /**
         * 获取国标行业分类
         * @returns {Array}
         */
        getProjectTypeData: function () {
            var vm = this;
            var list = [];
            $.ajax({
                url: ctx + 'agsupport/dic/getAgDicByTypeCode/GBHYDMFBND-2017',
                type: 'get',
                async: true,
                dataType: "json",
                success: function (result) {
                    if (result.success) {
                        list = result.content == "" ? [] : result.content;
                    }
                    Vue.set(vm.complianceCheck, 'projectDatas', list);
                },
                error: function (e) {
                    vm.$message({
                        showClose: true,
                        message: '服务器异常！',
                        type: 'error'
                    });
                }
            });
            return list;
        },
        /**
         * 项目类型切换
         * @param value
         */
        projectSelectChange: function (value) {
            var vm = this;
            vm.complianceCheck.projectSelectValue = value;
            vm.getProjectLayers();
        },
        getProjectLayers: function () {
            var projectLayers = [];
            var vm = this;
            $.ajax({
                url: ctx + '/agsupport/complianceCheck/getAllByItemCode',
                type: 'post',
                data: {itemCode: vm.complianceCheck.projectSelectValue},
                async: false,
                dataType: "json",
                success: function (result) {
                    if (result.success == true) {
                        projectLayers = JSON.parse(result.message);
                    } else {

                    }
                },
                error: function (e) {
                    vm.$message({
                        showClose: true,
                        message: '服务器异常！',
                        type: 'error'
                    });
                }
            });
            vm.complianceCheck.projectLayers = projectLayers;
            return projectLayers;
        },
        saveLayerProject: function () {
            var vm = this;
            var rows = vm.complianceCheck.projectLayers;
            if (rows.length == 0) {
                return;
            }
            var projectSelectData = {};
            for (var i = 0; i < vm.complianceCheck.projectDatas.length; i++) {
                if (vm.complianceCheck.projectDatas[i].value == vm.complianceCheck.projectSelectValue) {
                    projectSelectData = vm.complianceCheck.projectDatas[i];
                    break;
                }
            }
            $.each(rows, function (index, val) {
                val.itemCode = projectSelectData.value;
                val.itemName = projectSelectData.name;
            });
            $.ajax({
                url: ctx + '/agsupport/complianceCheck/saveOrUpdateProject',
                type: 'post',
                data: {listStr: JSON.stringify(rows)},
                async: false,
                dataType: "json",
                success: function (result) {
                    if (result.success == true) {
                        vm.$message({
                            showClose: true,
                            message: '更新保存成功！',
                            type: 'info'
                        });
                        vm.getProjectLayers();
                    } else {
                        vm.$message({
                            showClose: true,
                            message: '保存出错：' + result.message,
                            type: 'error'
                        });
                    }
                },
                error: function (e) {
                    vm.$message({
                        showClose: true,
                        message: '服务器异常！',
                        type: 'error'
                    });
                }
            });
        },
        /**
         * 根据类型获取用地分类数据
         * @param type
         * @returns {Array}
         */
        getTargetLandTypeTreeData: function (type,callback) {
            var vm = this;
            var typeTreeData = [];
            $.ajax({
                url: ctx + '/agsupport/landTypeStandards/getTargetLandTypeTreeData',
                type: 'post',
                data: {type: type, isDefault: 1, codeSort: "2"},
                async: true,
                dataType: "json",
                success: function (result) {
                    if (result.success == true) {
                        typeTreeData = JSON.parse(result.message);
                    }
                    callback(typeTreeData);
                },
                error: function (e) {
                    callback(typeTreeData);
                    vm.$message({
                        showClose: true,
                        message: '服务器异常！',
                        type: 'error'
                    });
                }
            });
        },
        rangeSelectFocus: function (row) {
            var vm = this;
            vm.complianceCheck.currRow = row;
            var datas = $.extend([], this.complianceCheck.treeData);
            Vue.set(vm.complianceCheck, 'currTreeData', datas);
            Vue.set(vm.complianceCheck, 'currTreechecked', row.rangeCode.split(","));
            var offset = $(window.event.currentTarget).offset();
            var height = window.event.currentTarget.offsetHeight;
            var width = window.event.currentTarget.clientWidth - 10;
            offset.top += height;
            $(".div-tree").show();
            $(".div-tree").offset(offset);
            $(".div-tree").css("width", width);
        },
        treeCheckChange: function (checked) {
            var vm = this;
            var nodes = this.$refs.tree.getCheckedNodes();
            vm.complianceCheck.rangeCodes = [];
            vm.complianceCheck.rangeNames = [];
            $.each(nodes, function (index, node) {
                if (node.id && node.code != "" && node.code != null) {
                    vm.complianceCheck.rangeCodes.push(node.code);
                    vm.complianceCheck.rangeNames.push(node.name);
                }
            });
        },
        operTreeDiv: function (oper) {
            var vm = this;
            if (oper == "sel") {
                vm.complianceCheck.currRow.rangeCode = vm.complianceCheck.rangeCodes.join(",");
                vm.complianceCheck.currRow.rangeCn = vm.complianceCheck.rangeNames.join(",");
            }
            $(".div-tree").hide();
        }
    },
    mounted: function () {
        var vm = this;
        this.getLayerTypes();
        this.getComplianceCheckLayers();
        this.getBoolean();
        this.getProjectTypeData();
        this.getTargetLandTypeTreeData("土规",function (types) {
            vm.complianceCheck.treeData.push({
                code: '土规',
                name: '土规',
                children: types
            });
        });
        var kgTypesTreeData = this.getTargetLandTypeTreeData("城规",function (types) {
            vm.complianceCheck.treeData.push({
                code: '城规',
                name: '城规',
                children: types
            });
        });
    }
});