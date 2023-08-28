<template>
  <div class="app-container">

  <!--表单查询-->
  <el-form :inline="true" class="demo-form-inline">
   <el-form-item>
      <el-input  v-model="searchObj.hosname" placeholder="医院名称"/>
   </el-form-item>
   <el-form-item>
      <el-input v-model="searchObj.hoscode" placeholder="医院编号"/>
   </el-form-item>
   <el-button type="primary" icon="el-icon-search" @click="getList()">查询</el-button>
</el-form>

<div>
  <el-button type="danger" size="mini" 
         icon="el-icon-delete" @click="removeRows()">批量删除</el-button>
</div>



  
  <!-- banner列表 -->
  <el-table
  :data="list" stripe style="width: 100%" @selection-change="handleSelectionChange">
  <el-table-column type="selection" width="55"/>
  <el-table-column type="index" width="50" label="序号"/>
  <el-table-column prop="hosname" label="医院名称"/>
  <el-table-column prop="hoscode" label="医院编号"/>
  <el-table-column prop="apiUrl" label="api基础路径" width="200"/>
  <el-table-column prop="contactsName" label="联系人姓名"/>
  <el-table-column prop="contactsPhone" label="联系人手机"/>
  <el-table-column label="状态" width="80">
  <template slot-scope="scope">
            {{ scope.row.status === 1 ? '可用' : '不可用' }}
  </template>
  </el-table-column>

  <!--功能键-->
  <el-table-column label="操作" width="280" align="center">
   <template slot-scope="scope">
      <el-button type="danger" size="mini" 
         icon="el-icon-delete" @click="removeDataById(scope.row.id)">删除</el-button>
      <el-button v-if="scope.row.status == 1" type="primary" size="mini" 
         icon="el-icon-star-on" @click="lockHospSet(scope.row.id,0)">锁定</el-button>
      <el-button v-if="scope.row.status == 0" type="primary" size="mini" 
         icon="el-icon-star-off" @click="lockHospSet(scope.row.id,1)">取消锁定</el-button>
      <router-link :to="'/hospSet/edit/'+scope.row.id">
          <el-button type="primary" size="mini" icon="el-icon-edit">编辑</el-button>
      </router-link>
   </template>
  </el-table-column>

  </el-table>

    <!-- 分页 -->
<el-pagination
  :current-page="current"
  :page-size="limit"
  :total="total"
  style="padding: 30px 0; text-align: center;"
  layout="total, prev, pager, next, jumper"
  @current-change="getList"/>
  </div>
  </template>
  
  
  <script>
  import hospset from '@/api/hospset'

  export default {
    data() {
      return {
        current:1,// 当前页
        limit:3, // 每页显示记录数
        searchObj:{}, // 条件封装对象
        list:[], //每页数据集合
        total:0, //总记录数
        multipleSelection:[]// 复选框 id

      }    
    },
    created() {
      this.getList()
    },
    methods: {
      // 医院设置列表
      getList(page = 1) {
        this.current = page
        hospset.getHospSetList(this.current,this.limit,this.searchObj)
               .then(response => {
                // 数据赋值给 list
                this.list = response.data.records
                this.total = response.data.total
               })
               .catch(response => {
                console.log(error)
               })
      },

      // 医院设置删除
      removeDataById(id){
        this.$confirm('此操作将永久删除医院是设置信息, 是否继续?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => { //确定执行then方法
        //调用接口
        hospset.deleteHospSet(id)
              .then(response => {
                // 提示
                this.$message({
                  type: 'success',
                  message: '删除成功!'
                })
                //刷新页面
                this.getList(this.current)
              })

      })
    },

      // 获取选择复选框的 id
      handleSelectionChange(selection){
        this.multipleSelection = selection
      },
      // 医院设置批量删除
      removeRows(){
        this.$confirm('此操作将永久删除医院是设置信息, 是否继续?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => { //确定执行then方法
          console.log(this.multipleSelection)
          var idList = []
          for(var i = 0; i < this.multipleSelection.length; i++){
            var obj = this.multipleSelection[i]
            var id = obj.id
            idList.push(id)
          }
        //调用接口
        hospset.batchRemoveHospSet(idList)
              .then(response => {
                // 提示
                this.$message({
                  type: 'success',
                  message: '删除成功!'
                })
                //刷新页面
                this.getList(this.current)
              })
      })
      },

      // 医院设置状态锁定
      lockHospSet(id,status){
        hospset.lockHospSet(id,status)
          .then(response => {
            this.$message({
                  type: 'success',
                  message: '修改成功!'
                })
            //刷新页面
            this.getList(this.current)
          })
      }


  }
}
  </script>
  