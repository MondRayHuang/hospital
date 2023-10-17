import request from '@/utils/request'


export default{
    //医院列表
    getHosplist(page,limit,searchObj){
        return request({
            url: `/admin/hosp/hospital/list/${page}/${limit}`,
            method: 'get',
            params: searchObj
        })
    },

    //根据 dictCode查询所有子节点（用于查询省）
    findByDictCode(dictCode){
        return request({
            url: `/admin/cmn/dict/findByDictCode/${dictCode}`,
            method: 'get'
        })
    },

    //根据数据 id 查询子数据列表(二级联动，用省的 id 作为 parent_id)
    findChildId(id){
        return request({
            url: `/admin/cmn/dict/findChildData/${id}`,
            method: 'get'
        })
    },

    //后端更新医院上线状态
    updateStatus(id,status){
        return request({
            url: `/admin/hosp/hospital/updateStatus/${id}/${status}`,
            method: 'get'
        })
    },

    //后端查看医院详情
    getHospById(id){
        return request({
            url: `/admin/hosp/hospital/showHospDetail/${id}`,
            method: 'get'
        })
    },

    //后端查看医院科室信息
    getDeptByHoscode(hoscode){
        return request({
            url: `/admin/hosp/department/getDeptList/${hoscode}`,
            method: 'get'
        })
    },

    //后端查看医院排班日期信息
    getScheduleRule(page,limit,hoscode,depcode){
        return request({
            url: `/admin/hosp/schedule/getScheduleRule/${page}/${limit}/${hoscode}/${depcode}`,
            method: 'get'
        })
    },

    //查询排班详情
    getScheduleDetail(hoscode,depcode,workDate){
        return request({
            url: `/admin/hosp/schedule/getScheduleDetail/${hoscode}/${depcode}/${workDate}`,
            method: 'get'
        })
    }
}