package org.pjj.commonutils;

/**
 * 定义 后台返回前台json 的状态码
 * @author PengJiaJun
 * @Date 2022/2/14 16:00
 */
public interface ResultCode {

    public static Integer SUCCESS = 20000;//成功

    public static Integer ERROR = 20001;//失败 (失败信息前端接收不到, 前端拦截器, 拦截返回code不为 20000 或 20002 根据就接收不到数据)

    public static Integer WARNING = 20002;//警告 (前端拦截器 兰姐code为20002的则 抛出异常表示 失败, 并返回信息)

}
// response 拦截器
//service.interceptors.response.use(
//        response => {
//        /**
//         * code为非20000是抛错 可结合自己业务进行修改
//         */
//        const res = response.data
//        if (res.code !== 20000) {
//
//        if(res.code === 20002) { // code为20002  抛出错误 并且 错误信息也返回
//        return Promise.reject(response.data)
//        }
//
//        Message({
//        message: res.message,
//        type: 'error',
//        duration: 5 * 1000
//        })
//
//        // 50008:非法的token; 50012:其他客户端登录了;  50014:Token 过期了;
//        if (res.code === 50008 || res.code === 50012 || res.code === 50014) {
//        MessageBox.confirm(
//        '你已被登出，可以取消继续留在该页面，或者重新登录',
//        '确定登出',
//        {
//        confirmButtonText: '重新登录',
//        cancelButtonText: '取消',
//        type: 'warning'
//        }
//        ).then(() => {
//        store.dispatch('FedLogOut').then(() => {
//        location.reload() // 为了重新实例化vue-router对象 避免bug
//        })
//        })
//        }
//        return Promise.reject('error')
//        } else {
//        return response.data
//        }
//        },
//        error => {
//        console.log('err' + error) // for debug
//        Message({
//        message: error.message,
//        type: 'error',
//        duration: 5 * 1000
//        })
//        return Promise.reject(error)
//        }
//        )
