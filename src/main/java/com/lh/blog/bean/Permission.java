package com.lh.blog.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "permission")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler"})
public class Permission  implements Serializable    {
    private static final long serialVersionUID = -7836722217413753561L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int mid;

    private int oid;

    @Transient
    private Module module;

    @Transient
    private Operation operation;

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return id == that.id &&
                mid == that.mid &&
                oid == that.oid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mid, oid);
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", mid=" + mid +
                ", oid=" + oid +
                ", module=" + module +
                ", operation=" + operation +
                '}';
    }
}
