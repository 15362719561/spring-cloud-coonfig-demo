var app = new Vue({
    el: '#app',
    data: {
        siteFactor: {
            treeData: [],
            layerTypes: [],
            titleVisible: false,
            toolVisible: true,
            layerViewVisible: true,
            rowStyle: {height: '50px'},
            tableHeight: window.document.body.clientHeight - 90,
            dataLoading: true,
            dataLoadingTip: '数据加载中...',
            expandRowKeys: [],
            multipleSiteFactorSelection: [],//选中的因子或因子目录
            allSelectedStatus: false,
            selSiteFactorParentId: "",
            emptySiteFactor: {  //空表单信息
                id: '',
                parentId: '',
                name: '',
                alias: '',
                url: '',
                remark: '',
                typeField: '',
                areaField: '',
                typeFieldCn: '',
                areaFieldCn: '',
                major: '',
                seq: '',
                type: '',
                source: ''
            },
            controlTypes: [],
            layerFields: [],
            booleanDatas: [],
            effectDatas: [],//因子效应下拉数据
            editLayerProjectViewVisible: false,
            projectDatas: [],
            projectSelectValue: '',
            projectSiteFactorHeight: 100,
            projectSiteFactors: [],
            currRow: {},
            landTypeTreeData: [],
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
        multipleLayerSelection: [],
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
                        Vue.set(vm.siteFactor,'layerTypes',result.content);
                    }
                },
                error: function (e) {

                }
            });
        },

        loadTreeData: function () {
            var vm = this;
            $.ajax({
                url: ctx + '/agsupport/siteFactor/getTreeData',
                type: 'post',
                async: false,
                dataType: "json",
                success: function (result) {
                    vm.siteFactor.dataLoading = false;
                    if (result.success) {
                        result = typeof result.message === 'string' ? JSON.parse(result.message) : result.message;
                        Vue.set(vm.siteFactor, 'treeData', result);
                    }
                },
                error: function () {
                    vm.siteFactor.dataLoading = false;
                }
            });
        },
        onTableRowSelectionChange: function (selection) {
            this.siteFactor.multipleSiteFactorSelection = selection;
        },
        /**
         * table行勾选事件
         * @param selection
         * @param row
         */
        onTableRowSelect: function (selection, row) {
            /**
             var vm = this;
             if(row.checked === undefined) {
                row.checked = false;
            }
             row.checked = ! row.checked;
             var tableRowSelectIterator = function(row){
                var selectStatus = row.checked;
                var childNodes  = vm.getAllChild(row);
                for(var i = 0; i < childNodes.length; i++) {
                    childNodes[i].checked = selectStatus;
                    vm.$refs.siteFactorTable.toggleRowSelection(childNodes[i],selectStatus);
                }
            };
             tableRowSelectIterator(row);
             **/
        },
        /**
         * 获取当前节点的子节点
         * @param row
         * @returns {Array}
         */
        getAllChild: function (row) {
            var childNodes = [];
            if (!row.children) {
                return [row];
            }
            var targetTreeData = row.children;
            var iterator = function (targetTreeNode) {
                for (var i = 0; i < targetTreeNode.length; i++) {
                    var currentTreeNode = targetTreeNode[i];
                    childNodes.push(currentTreeNode);
                    if (currentTreeNode.children && currentTreeNode.children.length > 0) {
                        iterator(currentTreeNode.children);
                    }
                }
            };
            iterator(targetTreeData);
            return childNodes;
        },
        /**
         * 全选/反选事件
         * @param selection
         */
        onTableSelectAll: function (selection) {
            var vm = this;
            vm.siteFactor.allSelectedStatus = !vm.siteFactor.allSelectedStatus;
            var selected = vm.siteFactor.allSelectedStatus;
            var iterator = function (currentTreeNode) {
                if (currentTreeNode.children && currentTreeNode.children.length > 0) {
                    for (var i = 0; i < currentTreeNode.children.length; i++) {
                        iterator(currentTreeNode.children[i]);
                    }
                }
                vm.$refs.siteFactorTable.toggleRowSelection(currentTreeNode, selected);
                currentTreeNode.checked = selected;
            };
            for (var i = 0; i < vm.siteFactor.treeData.length; i++) {
                iterator(vm.siteFactor.treeData[i]);
            }
        },
        /**
         * 获取管控类型下拉框
         */
        getControlTypes: function () {
            var vm = this;
            var controlTypes = [];
            $.ajax({
                url: ctx + '/agsupport/siteFactor/getControlTypes',
                type: 'post',
                async: false,
                dataType: "json",
                success: function (data) {
                    controlTypes = data;
                },
                error: function () {
                    vm.$message({
                        showClose: true,
                        message: '获取管控类型出错！',
                        type: 'error'
                    });
                }
            });
            return controlTypes;
        },
        //获取服务字段
        getLayerFielsByType: function (url,layerType) {
            var vm = this;
            var fields = [];
            if (url != "") {
                $.ajax({
                    url: ctx + '/agsupport/mapService/getFieldsByLayerType',
                    type: 'post',
                    data: {url: url, layerType: layerType},
                    async: true,
                    success: function (data) {
                        var result = JSON.parse(data);
                        if (result.success == true) {
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
                        vm.$message({
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
         * 根据编码获取数据字典配置
         * @param code A006:是否值,effect:辅助选址效应类型
         * @returns {Array}
         */
        getAgDicByTypeCode: function (code) {
            var list = [];
            $.ajax({
                url: ctx + 'agsupport/dic/getAgDicByTypeCode/' + code,
                type: 'get',
                async: false,
                dataType: "json",
                success: function (result) {
                    if (result.success) {
                        list = result.content == "" ? [] : result.content;
                    }
                },
                error: function (e) {
                }
            });
            return list;
        },
        /**
         * 切换地块筛选图层
         * @param val
         * @param row
         */
        toggleIsDataLayer: function (val, row) {
            var vm = this;
            $.each(vm.siteFactor.treeData, function (index, layerData) {
                var array = vm.getAllChild(layerData);
                $.each(array, function (index, layerData1) {
                    if (val == "1" && row.id != layerData1.id) {
                        layerData1.isDataLayer = "0";
                    }
                });
            });
        },
        checkSelCatalog: function () {
            var vm = this;
            var parentId = "";
            var seldatas = vm.siteFactor.multipleSiteFactorSelection;
            if (seldatas.length == 0 || seldatas.length > 1) {
                vm.$message({
                    showClose: true,
                    message: '您未选择或选择多个，请先选择一个目录！',
                    type: 'info'
                });
            } else {
                if (seldatas[0].type == "1" && seldatas[0].id != "") {
                    parentId = seldatas[0].id;
                }
                if (parentId == "") {
                    vm.$message({
                        showClose: true,
                        message: '未选择目录或当前目录未保存，请先保存！',
                        type: 'info'
                    });
                }
            }
            return parentId;
        },
        /**
         * 新增目录或节点
         * @param type catalog目录,node节点
         */
        addParam: function (type) {
            var vm = this;
            var parentId = "";
            var data = Object.assign({}, vm.siteFactor.emptySiteFactor);
            var seq = vm.siteFactor.treeData.length + 1;
            if (type == "catalog") {
                data.type = '1';
                data.name = "新增目录";
                data.children = [];
                $.each(vm.siteFactor.treeData, function (index, val) {
                    if (val.seq >= seq) {
                        seq = val.seq + 1;
                    }
                });
                data.seq = seq;
                vm.siteFactor.treeData.push(data);
            } else {
                vm.siteFactor.selSiteFactorParentId = vm.checkSelCatalog();
                vm.siteFactor.expandRowKeys = vm.siteFactor.expandRowKeys.filter(function (value) {
                    return value != vm.siteFactor.selSiteFactorParentId;
                });
                vm.siteFactor.expandRowKeys.push(vm.siteFactor.selSiteFactorParentId);
                if (vm.siteFactor.selSiteFactorParentId != "") {
                    data.name = "新增因子";
                    data.type = '0';
                    data.parentId = vm.siteFactor.selSiteFactorParentId;
                    $.each(vm.siteFactor.multipleSiteFactorSelection[0].children, function (index, val) {
                        if (val.seq >= seq) {
                            seq = val.seq + 1;
                        }
                    });
                    data.seq = seq;
                    vm.siteFactor.multipleSiteFactorSelection[0].children.push(data);
                }
            }
        },
        /**
         * 保存操作
         */
        saveParamItem: function () {
            var vm = this;
            var datas = [];
            var message = "";
            $.each(vm.siteFactor.treeData, function (index, val) {
                datas.push(val);
                var array = vm.getAllChild(val);
                datas = datas.concat(array);
            });
            var checkResult = true;
            for (var i = 0; i < datas.length; i++) {
                if (datas[i].name == "") {
                    checkResult = false;
                    message = '因子或目录名称不能为空，请检查输入！';
                    break;
                } else if (datas[i].type == 0 && datas[i].url == "") {
                    checkResult = false;
                    message = '因子服务地址不能为空，请检查输入！';
                    break;
                } else if (datas[i].type == 0 && datas[i].typeField == "") {
                    checkResult = false;
                    message = '因子类型字段不能为空，请检查输入！';
                    break;
                }
            }
            if (!checkResult) {
                vm.$message({
                    showClose: true,
                    message: message,
                    type: 'info'
                });
            }
            if (checkResult && datas.length > 0) {
                vm.saveOrUpdateSiteFactor(datas);
            }
        },
        saveOrUpdateSiteFactor: function (datas) {
            var vm = this;
            $.ajax({
                url: ctx + '/agsupport/siteFactor/saveOrUpdate',
                type: 'post',
                data: {
                    dataStr: JSON.stringify(datas)
                },
                async: false,
                dataType: "json",
                success: function (result) {
                    if (result.success == true) {
                        vm.$message({
                            showClose: true,
                            message: "保存成功",
                            type: 'info'
                        });
                        vm.loadTreeData();
                    } else {
                        vm.$message({
                            showClose: true,
                            message: '保存出错！',
                            type: 'error'
                        });
                    }
                },
                error: function () {
                    vm.$message({
                        showClose: true,
                        message: "服务器异常！",
                        type: 'error'
                    });
                }
            });
        },
        /**
         * 从专题数据里导入检测图层
         */
        importLayerView: function () {
            var vm = this;
            vm.layerKeyword = '';
            vm.layerTableCurrentPage = 1;
            vm.layerTablePageSize = 10;
            vm.siteFactor.selSiteFactorParentId = vm.checkSelCatalog();
            if (vm.siteFactor.selSiteFactorParentId != "") {
                vm.layerDialogVisible = true;
                vm.layerShowTable();
            }
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
                    name: layer.nameCn,
                    layerType: layerType,
                    type: '0',
                    parentId: vm.siteFactor.multipleSiteFactorSelection[0].id,
                    source: layer.id
                }
                if(layerType === '020202') { //ArcGIS MapServer(Dynamic)
                    layerObj.url = url + '/' + layerTable;
                }else if(layerType ==='040002') { //ArcGIS WFS
                    layerObj.url = url + '?' + "typeName=" + layerTable;
                }else if(layerType === '040003') { //GeoServer WFS
                    layerObj.url = url + '?' + "typeName=" + layerTable;
                }else if(layerType === '030003') { //GeoServer WMS
                    layerObj.url = url + '?' + "typeName=" + layerTable;
                }
                layers.push(layerObj);
            });
            if (layers.length === 0)  return;
            vm.siteFactor.expandRowKeys = vm.siteFactor.expandRowKeys.filter(function (value) {
                return value != vm.siteFactor.selSiteFactorParentId;
            });
            vm.siteFactor.expandRowKeys.push(vm.siteFactor.selSiteFactorParentId);
            $.ajax({
                url: ctx + '/agsupport/siteFactor/saveLayers',
                type: 'post',
                data: {
                    layersStr: JSON.stringify(layers),
                    parentId: vm.siteFactor.selSiteFactorParentId
                },
                async: false,
                success: function (data) {
                    vm.$message({
                        showClose: true,
                        message: '导入成功！',
                        type: 'info'
                    });
                    vm.layerDialogVisible = false;
                    vm.loadTreeData();
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
        setFieldsBylayerUrl: function (row) {
            var fields = this.getLayerFielsByType(row.url,row.layerType);
            Vue.set(this.siteFactor, 'layerFields', fields);
        },
        /**
         * 项目与检测图层关联
         */
        layerProjectView: function () {
            var vm = this;
            vm.siteFactor.editLayerProjectViewVisible = true;
            if (vm.siteFactor.projectSelectValue != "") {
                vm.loadSiteFactorByProject();
            }
        },
        /**
         * 删除因子或因子目录
         */
        deleteSiteFactor: function () {
            var vm = this;
            var itemIds = [];
            $.each(vm.siteFactor.multipleSiteFactorSelection, function (index, val) {
                itemIds.push(val.id);
            });
            if (itemIds.length == 0) {
                return;
            }
            vm.$confirm('此操作将永久删除当前选中数据, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(function () {
                $.post(ctx + '/agsupport/siteFactor/delete', {paramIds: itemIds.join(",")}, function (result) {
                    if (result.success) {
                        vm.$message({
                            showClose: true,
                            message: '删除成功！',
                            type: 'success'
                        });
                        vm.loadTreeData();
                    }
                }, 'json');
            });
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
                async: false,
                dataType: "json",
                success: function (result) {
                    if (result.success) {
                        list = result.content == "" ? [] : result.content;
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
            return list;
        },
        /**
         * 项目类型切换
         * @param value
         */
        projectSelectChange: function (value) {
            var vm = this;
            vm.siteFactor.projectSelectValue = value;
            $.each(vm.siteFactor.controlTypes, function (index, controlType) {
                var projectSiteFactors = vm.getSiteFactorByProject(controlType.value);
                controlType.projectSiteFactors = projectSiteFactors;
            });
        },
        /**
         * 根据国行分类获取因子信息
         * @returns {Array}
         */
        getSiteFactorByProject: function (contorlType) {
            var projectSiteFactors = [];
            var vm = this;
            $.ajax({
                url: ctx + '/agsupport/siteFactor/getTreeDataByContorlTypeAndProjectCode',
                type: 'post',
                data: {projectCode: vm.siteFactor.projectSelectValue, contorlType: contorlType},
                async: false,
                dataType: "json",
                success: function (result) {
                    if (result.success == true) {
                        projectSiteFactors = JSON.parse(result.message);
                    } else {
                        vm.$message({
                            showClose: true,
                            message: '查询关联因子出错：' + result.message,
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
            return projectSiteFactors;
        },
        saveProjectSiteFactor: function () {
            var vm = this;
            var datas = [];
            $.each(vm.siteFactor.controlTypes, function (index, controlType) {//所有管控分类
                var projectSiteFactors = controlType.projectSiteFactors;
                $.each(projectSiteFactors, function (index, projectSiteFactor) {//所有管控分类
                    var siteFactors = vm.getAllChild(projectSiteFactor);
                    datas = datas.concat(siteFactors);
                });
            });
            if (datas.length == 0) {
                return;
            }
            var projectSelectData = {};
            for (var i = 0; i < vm.siteFactor.projectDatas.length; i++) {
                if (vm.siteFactor.projectDatas[i].value == vm.siteFactor.projectSelectValue) {
                    projectSelectData = vm.siteFactor.projectDatas[i];
                    break;
                }
            }
            var weightSum = 0;
            $.each(datas, function (index, siteFactor) {
                siteFactor.projectCode = projectSelectData.value;
                siteFactor.projectName = projectSelectData.name;
                if (siteFactor.major != "1") {//必要管控
                    var weight = siteFactor.weight == "" ? 0 : Number(siteFactor.weight);
                    weightSum += weight;
                }
            });
            weightSum = Number(weightSum.toFixed(2));
            if (weightSum != 1) {
                vm.$message({
                    showClose: true,
                    message: '权重之和不为1，请调整各权重！',
                    type: 'error'
                });
                return;
            }
            $.ajax({
                url: ctx + '/agsupport/siteFactor/saveOrUpdateProject',
                type: 'post',
                async: false,
                dataType: "json",
                data: {listStr: JSON.stringify(datas)},
                success: function (result) {
                    if (result.success == true) {
                        vm.$message({
                            showClose: true,
                            message: "保存成功",
                            type: 'info'
                        });
                    } else {

                    }
                },
                error: function () {
                    vm.$message({
                        showClose: true,
                        message: "服务器异常",
                        type: 'info'
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
                data: {type: type, isDefault: 1, codeSort: 2},
                async: true,
                dataType: "json",
                success: function (result) {
                    if (result.success == true) {
                        typeTreeData = JSON.parse(result.message);
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
            return typeTreeData;
        },
        rangeSelectFocus: function (row) {
            var vm = this;
            row.typeWhere = row.typeWhere ? row.typeWhere : "";
            vm.siteFactor.currRow = row;
            var datas = $.extend([], this.siteFactor.landTypeTreeData);
            Vue.set(vm.siteFactor, 'currTreeData', datas);
            Vue.set(vm.siteFactor, 'currTreechecked', row.typeWhere.split(","));
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
            vm.siteFactor.rangeCodes = [];
            vm.siteFactor.rangeNames = [];
            $.each(nodes, function (index, node) {
                if (node.id && node.code != "" && node.code != null) {
                    vm.siteFactor.rangeCodes.push(node.code);
                    vm.siteFactor.rangeNames.push(node.name);
                }
            });
        },
        operTreeDiv: function (oper) {
            var vm = this;
            if (oper == "sel") {
                vm.siteFactor.currRow.typeWhere = vm.siteFactor.rangeCodes.join(",");
            }
            $(".div-tree").hide();
        }
    },
    mounted: function () {
        this.getLayerTypes();
        this.loadTreeData();
        this.siteFactor.booleanDatas = this.getAgDicByTypeCode("A006");
        this.siteFactor.effectDatas = this.getAgDicByTypeCode("effect");
        this.siteFactor.controlTypes = this.getControlTypes();
        this.siteFactor.projectDatas = this.getProjectTypeData();
        var tgTypesTreeData = this.getTargetLandTypeTreeData("土规");
        var kgTypesTreeData = this.getTargetLandTypeTreeData("城规");
        this.siteFactor.landTypeTreeData.push({
            code: '土规',
            name: '土规',
            children: tgTypesTreeData
        });
        this.siteFactor.landTypeTreeData.push({
            code: '城规',
            name: '城规',
            children: kgTypesTreeData
        });
    }
});