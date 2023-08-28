import request from '@/utils/request'

export default{
    // 医院设置列表
    getHospSetList(current,limit,searchObj) {
        return request({
            url: `/admin/hosp/hospitalSet/findPageHospSet/${current}/${limit}`,
            method: 'post',
            data: searchObj
        })
    },

    // 医院设置删除
    deleteHospSet(id) {
        return request({
            url: `/admin/hosp/hospitalSet/${id}`,
            method: 'delete'
        })
    },

    // 医院设置批量删除
    batchRemoveHospSet(idList){
        return request({
            url:`/admin/hosp/hospitalSet/batchRemove`,
            method:'delete',
            data: idList
        })

    },

    // 医院设置锁定与取消锁定
    lockHospSet(id,status){
        return request({
            url:`/admin/hosp/hospitalSet/lockHospitalSet/${id}/${status}`,
            method:'put'
        })
    },

    // 医院设置添加功能
    saveHospSet(hospitalSet){
        return request({
            url:`/admin/hosp/hospitalSet/saveHospitalSet`,
            method:'post',
            data: hospitalSet
        })
    },

    // 医院设置修改功能
    updateHospSet(hospitalSet){
        return request({
            url:'/admin/hosp/hospitalSet/updateHospitalSet',
            method:'post',
            data:hospitalSet
        })
    },

    // 医院设置详细信息
    getHospSet(id){
        return request({
            url: `/admin/hosp/hospitalSet/getHospSet/${id}`,
            method: 'get'
        })
    }
} 
