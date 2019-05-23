package cn.czc.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;

import cn.czc.jdbcutils.JDBCUtils;
import cn.czc.pojo.Pojo;

public class Split {

	private static QueryRunner qr = new QueryRunner(JDBCUtils.getDataSource());

	public static void main(String[] args) {
		
		while (true) {
			System.out.println("请输入对应数字后按回车键,选择要进行的操作\r\n1.添加数据\t2.更新数据\t3.删除数据\t4.查找数据\t5.退出");
			Scanner scanner = new Scanner(System.in);
			String chose = scanner.nextLine().trim();
			switch (chose) {
			// 1.添加数据
			case "1":
				add();
				break;
			// 2.更新数据
			case "2":
				update();
				break;
			// 3.删除数据
			case "3":
				delete();
				break;
			//查找数据
			case "4":
				select();
				break;
			//5.退出
			case "5":
				System.out.println("退出成功!");
				System.exit(0);
				break;
			default:
			}
		}

	}


	private static void update() {
		try {
			// 录入多行数据
			List<String> a = write();
			// 分割文字,获得Pojo
			List<Pojo> list = split(a);
			Pojo pojo1 = list.get(0);
			// 检查是否已经插入当日数据
			if (selectByDay(pojo1.getMonth(), pojo1.getDay()).size() != 0) { // check为真,库中已有当日数据
				deleteByDay(pojo1.getMonth(), pojo1.getDay());
			}
			for (Pojo pojo : list) {
				if (pojo == null) {
					// 输入文字不合格,重新输入
					System.out.println("输入文字不合格,重新输入");
					add();
				}
				// 向数据库插入数据
				insert(pojo);
			}
			System.out.println("数据更新完成");
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}

	private static void delete() {
		try {
			System.out.println("请输入要删除的日期(格式11-01):");
			Scanner scanner = new Scanner(System.in);
			String date = scanner.nextLine().trim();
			String month = date.split("-")[0];
			String day = date.split("-")[1];
			// 按天查询查询数据,从1号查到三十号
			deleteByDay(month, day);
			System.out.println("删除成功,已删除"+month+"月"+day+"日的数据.");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// 4.根据月份查询数据
	private static void select() {
		try {
			System.out.println("请输入要查询的月份:");
			Scanner scanner = new Scanner(System.in);
			String month = scanner.nextLine().trim();
			List<String> days = selectByMonth(month);
			// 按录入的第一天查询数据
			List<Pojo> list = selectByDay(month, days.get(days.size()-1));
			// 显示一天的数据
			// 首行数据显示月+人和任务
			if (list != null) {
				System.out.print(month + "月" + "\t");
				for (Pojo pojo : list) {
					System.out.print(pojo.getName() + pojo.getTask() + "\t");
				}
				// 第二行开始显示任务完成情况
				for (String d : days) {
					List<Pojo> list2 = selectByDay(month, String.valueOf(d));
					// 显示换行+日
					System.out.print("\r\n" + list2.get(0).getDay() + "日" + "\t");
					// 遍历显示完成情况
					for (Pojo pojo : list2) {
						System.out.print(pojo.getStatus() + "\t");
					}
				}
				System.out.println();
			} else {
				System.out.println("查询不到数据");
				System.out.println();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// 1.添加数据
	private static void add() {
		try {
			// 录入多行数据
			List<String> a = write();
			// 分割文字,获得Pojo
			List<Pojo> list = split(a);
			Pojo pojo1 = list.get(0);
			// 检查是否已经插入当日数据
			if (selectByDay(pojo1.getMonth(), pojo1.getDay()).size() != 0) { // check为真,库中已有当日数据
				System.out.println("当天数据已录入,数据添加失败!!请选择更新数据");
				return;
			}
			
			for (Pojo pojo : list) {
				if (pojo == null) {
					// 输入文字不合格,重新输入
					System.out.println("输入文字不合格,重新输入");
					add();
				}
				// 向数据库插入数据
				insert(pojo);
			}
			System.out.println("数据添加完成");//是否查询当月数据:y/n
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// 输入多行文字
	private static List<String> write() {
		System.out.println("请输入打卡文字,按两次回车键添加数据:");
		//StringBuilder stringbuilder = new StringBuilder();
		Scanner scanner = new Scanner(System.in);
		List<String> list = new ArrayList<>();
		while (true) {
			String text = scanner.nextLine().trim();
			if ("".equals(text)) {
				break;
			}
			list.add(text);
			//stringbuilder.append("/" + text);
		}

		//System.out.println(stringbuilder.toString());
		return list;
	}

	private static List<Pojo> split(List<String> a) {
		//String[] b = a.split("/");
		// 10月31日打卡:
		String[] dateTemp = a.get(0).split("月");
		String month = dateTemp[0];
		String day = dateTemp[1].split("日")[0];
		List<Pojo> list = new ArrayList<>();
		for (int i = 1; i < a.size(); i++) { // 成成:运动-,学习-
			String[] c = a.get(i).split(":"); // "成成","运动-,学习-"
			String name = c[0];
			String d = c[1]; // "运动-,学习-"
			String[] tasks = d.split(","); // "运动-","学习-"
			for (int j = 0; j < tasks.length; j++) {
				String[] e = tasks[j].split("-");
				String task = e[0];
				if (e.length > 1) {
					String status = e[1];
					if ("完成".equals(status)) {
						status = "1";
					}
					// System.out.println(name+"~"+task+"~"+status);
					Pojo pojo = new Pojo(month, day, name, task, status);
					list.add(pojo);
				} else {
					String status = "";
					Pojo pojo = new Pojo(month, day, name, task, status);
					list.add(pojo);
				}

			}
		}
		return list;
	}

	// 根据月查询已有数据的日
	private static List<String> selectByMonth(String month) {
		String sql = "SELECT DISTINCT day FROM task11 WHERE month=? ORDER BY DAY";
		Object[] params = { month };
		try {
			List<String> list = qr.query(sql, new ColumnListHandler<String>(), params);
			return list;
		} catch (SQLException ex) {
			throw new RuntimeException("数据查询失败");
		}
	}

	// 根据月日查询当日数据
	private static List<Pojo> selectByDay(String month, String day) {
		String sql = "SELECT * FROM task11 WHERE month = ? AND day = ?";
		Object[] params = { month, day };
		try {

			List<Pojo> list = qr.query(sql, new BeanListHandler<Pojo>(Pojo.class), params);
			return list;
		} catch (SQLException ex) {
			System.err.println("数据查询失败");
			select();
			throw new RuntimeException("数据查询失败");
		}
	}

	// 添加数据
	private static void insert(Pojo pojo) {
		String sql = "INSERT INTO task11 (month,day,name,task,status) VALUES (?,?,?,?,?)";
		Object[] params = { pojo.getMonth(), pojo.getDay(), pojo.getName(), pojo.getTask(), pojo.getStatus() };
		try {
			qr.update(sql, params);
			// System.out.println("数据添加完成");
		} catch (SQLException ex) {
			throw new RuntimeException("数据添加失败");
		}
	}
	//删除数据
	private static void deleteByDay(String month, String day) {
		String sql = "DELETE FROM task11 WHERE month=? AND day = ?";
		Object[] params = { month,day };
		try {
			qr.update(sql, params);
			// System.out.println("");
		} catch (SQLException ex) {
			throw new RuntimeException("数据删除失败");
		}
	}

}
