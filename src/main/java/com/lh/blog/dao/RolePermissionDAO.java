package com.lh.blog.dao;

import com.lh.blog.bean.RolePermission;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePermissionDAO extends JpaRepository<RolePermission,Integer> {

    public List<RolePermission> findAllByRid(int rid, Sort sort);

    public RolePermission findByRidAndPid(int rid, int pid);
}
