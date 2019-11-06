var app = new Vue({
    el: '#app',
    data: {
       landTypeStandards: {
           allLandTypeList: [], //全部的用地类型配置
           landTypeTreeData: [],
           landTypeTreeDataClone:[],
           landTypeSearch: '', //查询关键字
           landTypesDataLoading: true,
           landTypesDataLoadingTip: '数据加载中...',
           allSelectedStatus: false,
           hasCheckedNodes: false, //是否有选中的节点
           landTypesTableHeight: window.document.body.clientHeight - 90,
           rowStyle: {height: '50px'},
           isAddStatus: false,
           expandRowKeys: [],
           types: [],
           selectType: '',
           versions: [],
           selectVersion: ''
       }
    },
    methods: {

        /**
         * 初始化用地类型标准配置表
         */
        getLandTypeAllList: function() {
            var vm = this;
            vm.landTypeStandards.landTypesDataLoading = true;
            $.ajax({
                url: ctx + '/agsupport/landTypeStandards/getAllList',
                type: 'post',
                async: true,
                dataType: "json",
                success: function (result) {
                    if(result.success) {
                        result.message = JSON.parse(result.message);
                    }
                    Vue.set(vm.landTypeStandards,'allLandTypeList',result.message);
                    vm.landTypeStandards.landTypesDataLoading = false;
                },
                error: function (e) {
                    vm.$message({
                        showClose: true,
                        message: '加载数据失败！',
                        type: 'error'
                    });
                }
            });
        },

        /**
         * 初始化用地类型标准配置表(树结构)
         */
        loadLandTypeTreeData: function() {
            var vm = this;
            vm.landTypeStandards.landTypesDataLoading = true;
            $.ajax({
                url: ctx + '/agsupport/landTypeStandards/getLandTypeTreeData',
                type: 'post',
                async: true,
                cache: false,
                data: {},
                dataType: "json",
                success: function (result) {
                    if(result.success) {
                        Vue.set(vm.landTypeStandards,'landTypeTreeDataClone',JSON.parse(result.message));
                        result.message = JSON.parse(result.message);
                    }
                    var landTypeTreeData = result.message;
                    Vue.set(vm.landTypeStandards,'landTypeTreeData',landTypeTreeData);
                    Vue.set(vm.landTypeStandards,'expandRowKeys',[]);
                    vm.getTypes();
                    vm.getLandTypeAllList();
                },
                error: function (e) {
                    vm.$message({
                        showClose: true,
                        message: '加载数据失败！',
                        type: 'error'
                    });
                    vm.landTypeStandards.landTypesDataLoading = false;
                }
            });
        },

        /**
         * 获取所有类型
         */
        getTypes: function(){
            var vm = this;
            $.ajax({
                url: ctx + '/agsupport/landTypeStandards/getAllTypes',
                type: 'get',
                async: true,
                dataType: "json",
                success: function (result) {
                    if(result.success) {
                        result.message = JSON.parse(result.message);
                    }
                    Vue.set(vm.landTypeStandards,'types',result.message);
                }
            })
        },

        /**
         * 根据类型获取版本
         * @param type
         */
        getVersions: function(type){
            var vm = this;
            $.ajax({
                url: ctx + '/agsupport/landTypeStandards/getVersionsByType',
                type: 'get',
                async: true,
                data: {type: type},
                dataType: "json",
                success: function (result) {
                    if(result.success) {
                        result.message = JSON.parse(result.message);
                    }
                    Vue.set(vm.landTypeStandards,'versions',result.message);
                }
            })
        },

        typeSelectChange: function(value){
            this.landTypeStandards.selectVersion = '';
            this.getVersions(value);
        },

        /**
         * 查询
         */
        searchLandType: function () {
            var selectedType = this.landTypeStandards.selectType;
            var selectedVersion = this.landTypeStandards.selectVersion;
            if(selectedType === '') return;
            var treeData = this.landTypeTreeDataFilter(selectedType,selectedVersion);
            Vue.set(this.landTypeStandards,'landTypeTreeData',treeData);
        },

        /**
         * 清除查询
         */
        clearSearchLandType: function () {
            this.landTypeStandards.selectType = '';
            this.landTypeStandards.selectVersion = '';
            var treeData = this.landTypeTreeDataFilter('','');
            Vue.set(this.landTypeStandards,'landTypeTreeData',treeData);
        },

        /**
         * 根据类型和版本过滤组装数据
         * @param selectedType
         * @param selectVersion
         * @returns {*[]}
         */
        landTypeTreeDataFilter: function(selectedType,selectVersion){
            var landTypeList = JSON.parse(JSON.stringify(this.landTypeStandards.allLandTypeList));
            var parents = [],childrens = [];
            if(selectedType === '') {
                parents = landTypeList.filter(function(item){
                    return item.parentId === '';
                });
                childrens = landTypeList.filter(function(item){
                    return Number(item.codeSort) >= 1;
                });
            }else {
                parents = landTypeList.filter(function(item){
                    if(item.codeSort === '0' && item.name !== selectedType) {
                        return false;
                    }else {
                        return item.parentId === '';
                    }
                });
                childrens = landTypeList.filter(function(item){
                    var boolean1 = Number(item.codeSort) >= 1 && item.type === selectedType;
                    var boolean2 =  selectVersion === '' ? true : item.versionCode === selectVersion;
                    return boolean1 && boolean2;
                });
            }

            var transIterator = function(parents,childrens){
                parents.forEach(function(currentParentItem){
                    childrens.forEach(function(currentChildItem,index){
                        if(currentChildItem.parentId === currentParentItem.id) {
                            var _children = JSON.parse(JSON.stringify(childrens))
                            _children.splice(index, 1);
                            transIterator([currentChildItem], _children);
                            if(currentParentItem.children && currentParentItem.children.length > 0) {
                                currentParentItem.children.push(currentChildItem);
                            }else {
                                currentParentItem.children = [currentChildItem];
                            }
                        }
                    });
                });
            }
            transIterator(parents,childrens);
            return parents;
        },


        /**
         * 列表数据转树状结构数据
         * @param landTypeList
         */
        getTransLandTypeListToTreeData: function(landTypeList){
            var parents = landTypeList.filter(function(item){
                return item.parentId === '';
            });
            var childrens = landTypeList.filter(function(item){
                return item.parentId !== '';
            });

            var transIterator = function(parents,childrens){
                parents.forEach(function(currentParentItem){
                    childrens.forEach(function(currentChildItem,index){
                        if(currentChildItem.parentId === currentParentItem.id) {
                            var _children = JSON.parse(JSON.stringify(childrens))
                            _children.splice(index, 1);
                            transIterator([currentChildItem], _children);
                            if(currentParentItem.children) {
                                currentParentItem.children.push(currentChildItem);
                            }else {
                                currentParentItem.children = [currentChildItem];
                            }
                        }
                    });
                });
            }
            transIterator(parents,childrens);
            return parents;
        },

        /**
         * 格式化类型（城规：1；土规：2）
         * @param row
         * @param column
         * @returns {string}
         */
        landTypeFormatter: function(row, column){
            if(row.type === '1') {
                return '城规';
            }else if(row.type === '0') {
                return '土规';
            }
        },

        /**
         * table行勾选事件
         * @param selection
         * @param row
         */
        onTableRowSelect: function(selection, row) {
            var vm = this;
            if(row.checked === undefined) {
                row.checked = false;
            }
            row.checked = ! row.checked;
            var tableRowSelectIterator = function(row){
                var selectStatus = row.checked;
                var childNodes  = vm.getChildLandTypesByCode(row);
                for(var i = 0; i < childNodes.length; i++) {
                    childNodes[i].checked = selectStatus;
                    vm.$refs.landTypeStandardsTable.toggleRowSelection(childNodes[i],selectStatus);
                }
            }
            tableRowSelectIterator(row);
        },

        /**
         * 获取当前选中的节点(改用$refs.landTypeStandardsTable.selection)
         */
        getCheckedNodes: function(){
            var landTypeTreeData = this.landTypeStandards.landTypeTreeData;
            var checkedNodes = [];
            var iterator = function(targetTreeNode){
                for(var i = 0; i < targetTreeNode.length; i++) {
                    var currentTreeNode = targetTreeNode[i];
                    if(currentTreeNode.checked) {
                        checkedNodes.push(currentTreeNode);
                    }
                    if(currentTreeNode.children && currentTreeNode.children.length > 0) {
                        iterator(currentTreeNode.children);
                    }
                }
            }
            iterator(landTypeTreeData);
            return checkedNodes;
        },

        /**
         * 获取当前节点的子节点
         * @param row
         * @returns {Array}
         */
        getChildLandTypesByCode: function(row) {
            var childNodes = [];
            if(!row.children) {
                return [row];
            }
            var targetTreeData = row.children;
            var iterator = function(targetTreeNode){
                for(var i = 0; i < targetTreeNode.length; i++) {
                    var currentTreeNode = targetTreeNode[i];
                    childNodes.push(currentTreeNode);
                    if(currentTreeNode.children && currentTreeNode.children.length > 0) {
                        iterator(currentTreeNode.children);
                    }
                }
            }
            iterator(targetTreeData);
            return childNodes;
        },

        /**
         * 全选/反选事件
         * @param selection
         */
        onTableSelectAll: function(selection){
            var vm = this;
            vm.landTypeStandards.allSelectedStatus = !vm.landTypeStandards.allSelectedStatus;
            var selected = vm.landTypeStandards.allSelectedStatus;
            var iterator = function(currentTreeNode){
                if(currentTreeNode.children && currentTreeNode.children.length > 0) {
                   for(var i = 0; i < currentTreeNode.children.length; i++) {
                       iterator(currentTreeNode.children[i]);
                   }
                }
                vm.$refs.landTypeStandardsTable.toggleRowSelection(currentTreeNode,selected);
                currentTreeNode.checked = selected;
            };
            for(var i = 0; i < vm.landTypeStandards.landTypeTreeData.length; i++) {
                iterator(vm.landTypeStandards.landTypeTreeData[i]);
            }
        },

        /**
         * 新增根节点
         */
        addParentLandType: function(){
            var item = {
                id:'',
                code: '',
                name: '',
                parentId: '',
                color: '',
                lineColor: '',
                type: '',
                versionCode: '',
                codeSort: '0',
                editStatus: true,
                isDefault: '0',
                rowKey:this.getRandomRowKey()
            }
            this.landTypeStandards.landTypeTreeData.unshift(item);
        },

        /**
         * 新增子节点
         * @param row
         */
        addChildLandType: function(row){
            var children = row.children;
            if(!children) {
                row.children = [];
            }else {
                var item = {
                    id:'',
                    code: row.code + '_',
                    name: '',
                    parentId: row.id,
                    color: '#ffffff',
                    lineColor: '#ffffff',
                    type: row.type,
                    versionCode: row.versionCode,
                    codeSort: Number(row.codeSort) + 1,
                    editStatus: true,
                    isDefault: row.isDefault,
                    rowKey:this.getRandomRowKey()
                }
                row.children.unshift(item);
                if(!this.landTypeStandards.expandRowKeys.indexOf(row.rowKey) > -1) {
                    this.landTypeStandards.expandRowKeys.push(row.rowKey);
                }
            }
            this.landTypeStandards.isAddStatus = true;
        },

        /**
         * 进入编辑状态
         */
        editLandType: function (row) {
            row.editStatus = true;
        },

        /**
         * 退出编辑状态
         * @param row
         */
        cancelEditLandType: function (row) {
            row.editStatus = false;
            this.loadLandTypeTreeData();
            this.landTypeStandards.isAddStatus = false;

        },

        /**
         * 保存
         * @param row
         */
        saveLandType: function (row) {
            var vm = this;
            if(row.parentId !== '') { //非根节点
                if(row.versionCode === '') {
                    row.versionCode = row.code;
                }
                if(row.type === '') {
                    row.type = row.name;
                }
            }else{ //根节点将名称保存到类型字段
                row.type = row.name;
            }
            if(row.name.trim() === '' || row.code.trim() === '') {
                this.$alert('用地性质名称和编码不能为空！', '系统提示', {
                    confirmButtonText: '确定'
                });
                return;
            }
            var data = JSON.stringify([row]);
            $.ajax({
                url: ctx + '/agsupport/landTypeStandards/saveOrUpdate',
                type: 'post',
                async: true,
                dataType: "json",
                data: {listStr: data},
                success: function (result) {
                    if(result.success) {
                        row.editStatus = false;
                        vm.$message({
                            showClose: true,
                            message: '保存成功！',
                            type: 'success'
                        });
                    }
                    vm.loadLandTypeTreeData();
                },
                error: function (e) {
                    vm.$message({
                        showClose: true,
                        message: '保存失败！',
                        type: 'error'
                    });
                }
            });
            this.landTypeStandards.isAddStatus = false;
        },

        /**
         * 删除节点
         */
        deleteLandTypes: function(){
            var vm = this;
            //var nodes = this.getCheckedNodes();
            var nodes = this.$refs.landTypeStandardsTable.selection;
            if(nodes.length === 0) {
                this.$alert('请选择一条数据', '系统提示', {
                    confirmButtonText: '确定'
                });
                return;
            }
            var ids = [];
            nodes.forEach(function(item){
                ids.push(item.id);
            });
            this.$confirm('此操作将永久当前删选中的数据, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(function(){
                $.ajax({
                    url: ctx + '/agsupport/landTypeStandards/delete',
                    type: 'post',
                    async: true,
                    dataType: "json",
                    data: {paramIds: ids.toString()},
                    success: function (result) {
                        if(result.success) {
                            vm.$message({
                                showClose: true,
                                message: '删除成功！',
                                type: 'success'
                            });
                            vm.loadLandTypeTreeData();
                        }
                    },
                    error: function (e) {
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
         * 切换默认版本
         */
        toggleDefaultLandVersion: function(val,row){
            var vm = this;
            var versionCode = row.versionCode;
            var isDefault = row.isDefault;
            var type = row.type;
            $.ajax({
                url: ctx + '/agsupport/landTypeStandards/toggleDefaultLandVersion',
                type: 'post',
                async: true,
                dataType: "json",
                data: {versionCode: versionCode,isDefault:isDefault,type:type},
                success: function(result){
                    if(result.success) {
                        vm.loadLandTypeTreeData();
                    }
                }
            });
        },

        getRandomRowKey: function (){
            function random() {
                return (((1 + Math.random())* 0x10000)|0).toString(16).substring(1);
            }
            return (random()+random()+"-"+random()+"-"+random()+"-"+random()+"-"+random()+random()+random());
        },
        onTableRowSelectionChange: function (selection) {
            console.log(selection.length);
        }


    },
    computed: {

    },
    mounted: function(){
        var vm = this;
        vm.loadLandTypeTreeData();
        this.landTypeStandards.landTypesTableHeight = window.document.body.clientHeight - 90;
        window.onresize = function(){
            vm.landTypeStandards.landTypesTableHeight = window.document.body.clientHeight - 90;
        }
    }
});