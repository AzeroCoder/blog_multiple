package com.lh.blog.controller.back;

import com.lh.blog.bean.Module;
import com.lh.blog.service.ModuleService;
import com.lh.blog.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ModuleController {
    @Autowired
    ModuleService moduleService;

    @GetMapping(value = "/admin/modules")
    public Map<String,Object> listModule(@RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "size", defaultValue = "7") int size) throws Exception {
        Map<String,Object> map = new HashMap<>();
        PageUtil<Module> page = moduleService.list(start,size,5);
        map.put("page",page);
        List<Module> modules = moduleService.list();
        map.put("modules",modules);
        return map;
    }

    @PostMapping(value = "/admin/modules")
    public void add(@RequestBody Module module)
    {
        moduleService.add(module);
    }

    @DeleteMapping(value = "/admin/modules/{id}")
    public String delete(@PathVariable("id")int id)
    {
        moduleService.delete(id);
        return null;
    }

    @PutMapping(value = "/adminmodules/{id}")
    public void update(@RequestBody Module module)
    {
        moduleService.update(module);
    }

    @GetMapping(value = "/admin/modules/{id}")
    public Module get(@PathVariable("id")int id)
    {
        return moduleService.get(id);
    }

    @PostMapping(value = "/admin/modules/search")
    public List<Module> search(@RequestParam("key")String key)
    {
        return moduleService.listByKey(key);
    }

}
