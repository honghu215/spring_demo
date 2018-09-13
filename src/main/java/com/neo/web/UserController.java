package com.neo.web;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.neo.entity.UserEntity;
import com.neo.mapper.UserMapper;

import javax.annotation.Resource;

/**
 *
 * @author shibin
 */
@RestController
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

	@Resource
	private UserMapper userMapper;

    /**
     * 查询所有用户
     * @return
     */
	@GetMapping("/getUsers")
	public List<UserEntity> getUsers() {
		List<UserEntity> users=userMapper.getAll();
		return users;
	}

    /**
     * 根据id 查询
     * @param id
     * @return
     */
    @GetMapping("/getUser")
    public UserEntity getUser(Long id) {
    	UserEntity user=userMapper.getOne(id);
        return user;
    }

    /**
     * 添加用户
     * @param user
     */
    @PostMapping("/add")
    public void save(UserEntity user) {
    	userMapper.insert(user);
    }

    /**
     * 更新
     * @param user
     */
    @PutMapping(value="update")
    public void update(UserEntity user) {
    	userMapper.update(user);
    }

    /**
     * 删除
     * @param id
     */
    @DeleteMapping(value="/delete/{id}")
    public void delete(@PathVariable("id") Long id) {
    	userMapper.delete(id);
    }


    /**
     * 1. synchronized修饰的方法 设置锁
     * @return
     */
    @GetMapping("/test1")
    public synchronized String test1() {

        logger.info("已锁住");
        for (int i=0;i<10;i++) {
            logger.info(String.valueOf(i));
            try {
                logger.info(Thread.currentThread().getName()+ "休眠5秒!");
                //放大代码块执行完成的时间，便于观察
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info("已放开");
        return "Success";
    }


    /**
     * 2. Semaphore是基于计数的信号量，它可以设定一个资源的总数量，基于这个总数量，多线程竞争获取许可信号，做自己的申请后归还，超过总数量后，线程申请许可，信号将会被阻塞。等到有资源时，继续执行
     * 这里你访问第一次后立马第二次请求会显示资源占用
     */
    Semaphore semaphore=new Semaphore(1);

    @GetMapping("/test2")
    public String test2(){

        //可用资源数
        int availablePermits = semaphore.availablePermits();
        if(availablePermits>0){
            logger.info("抢到资源");
        }else{
            logger.info("资源已被占用，稍后再试");
            return "Resource is busy！";
        }
        try {
            //请求占用一个资源
            semaphore.acquire(1);
            logger.info("资源正在被使用");
            //放大时间，观察
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            //释放一个资源
            semaphore.release(1);
        }
        logger.info("释放资源");
        return "Success";
    }
}