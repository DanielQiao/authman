package com.laoxu.java.authman.util;

import com.feilong.core.Validator;
import com.laoxu.java.authman.common.JSTreeEntity;
import com.laoxu.java.authman.common.Select2Entity;
import com.laoxu.java.authman.model.SysMenu;

import java.util.ArrayList;
import java.util.List;


public class TreeUtil {

	/*
	 * select2下拉组件数据对象
	 */
	private List<Select2Entity> selectTree = new ArrayList<Select2Entity>();
	/*
	 * 生成select2下拉组件数据时遍历的次数
	 */
	private int selectCnt = 0;

	public List<JSTreeEntity> generateJSTree(List<SysMenu> menuList)
	{
		List<JSTreeEntity> jstreeList = new ArrayList<JSTreeEntity>();

		for (SysMenu menu : menuList) {
			JSTreeEntity jstree = new JSTreeEntity();
			jstree.setId(menu.getId().toString());
			jstree.setParent(menu.getPid()==0?"#":menu.getPid().toString());
			jstree.setText(menu.getMenuName());
			jstree.setIcon(Validator.isNullOrEmpty(menu.getIcon()) ? "am-icon-cog" : menu.getIcon());
			JSTreeEntity.State state = new JSTreeEntity().new State();
			state.setDisabled(false);
			state.setSelected(menu.isChecked());
			state.setOpened(true);
			jstree.setState(state);
			jstreeList.add(jstree);
		}
		return jstreeList;
	}
	/**
	 * 根据父节点的ID获取所有子节点
	 * @param list	具有树形结构特点的集合
	 * @param parentId	父节点ID
	 * @return	树形结构集合
	 */
	public List<Select2Entity> getSelectTree(List<SysMenu> list,Integer parentId) {
		List<SysMenu> treeMenuList = treeMenuList(list, parentId);
		recursionForSelect(treeMenuList);
		return selectTree;
	}

	/**
	 * 递归列表
	 * @param list
	 */
	private void recursionForSelect(List<SysMenu> list) {
		// 应该是根据深度或层级来计算，比如：系统管理--》部门管理=》新增 selectCnt应该是2
		String str = "";
		for(int i=0; i< selectCnt; i++)
		{
			str += "├┈┈┈";
		}
		for (SysMenu menu : list) {
			// 权限类型代码作为层级
			selectCnt = menu.getMenuType();
			Select2Entity se = new Select2Entity();
			se.setId(menu.getId().toString());
			se.setText(str + menu.getMenuName());
			se.setName(menu.getMenuName());
			selectTree.add(se);
			if(menu.getChildren().size() > 0)
			{
				selectCnt ++;
				recursionForSelect(menu.getChildren());
			}
		}
	}

	/**
	 * 菜单树递归
	 * @param menuList
	 * @param parentId
	 * @return
	 */
	public List<SysMenu> treeMenuList(List<SysMenu> menuList, Integer parentId) {
		 List<SysMenu> childMenu = new ArrayList<SysMenu>();
	       for (SysMenu menu : menuList) {
	    	   Integer menuId = menu.getId();
	           Integer pid = menu.getPid();
	           if (parentId == pid) {
	        	   List<SysMenu> c_node = treeMenuList(menuList, menuId);
	        	   menu.setChildren(c_node);
	               childMenu.add(menu);
	           }
	       }
	       return childMenu;
	  }

}
