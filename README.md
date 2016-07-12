# ZhihuDemo

1. 使用SwipeRefreshLayout+RecyclerView展示数据
2. 展示数据的布局分为三种:
	+ 带日期,有图片,有标题内容的
	+ 不带日期的,有图片,有标题内容的
	+ 用于显示上拉加载更多的footer
3. 在收藏界面中,使用ItemTouchHelper+ItemTouchHelper.Callback实现简单的滑动删除某一个item
4. 使用RxJava实现在非UI线程上对数据的加载
5. 主界面的网络加载数据分为三种:
	+ 当前界面为空,只加载当天的消息
	+ 上拉刷新,加载前一天的内容(比如当前页面显示的是7月5日的消息,上拉刷新显示7月4日),这里需要用SharedPreferences来保存需要上拉刷新的是哪一天的内容
	+ 下拉刷新,当7月10日打开app加载完10号的消息时,7月12号再打开下拉刷新时,即可刷新出7月12日,7月11日的消息,这里就需要数据储存保留上一次最新加载的消息的日期.
	+ 关键点:这里每个日期都对应着一个地址http://news.at.zhihu.com/api/4/news/before/20160710,对返回的字符串解析得到一个List,多个地址就要把多个List整成一个再到UI界面刷新内容,一开始用的是AsyncTask来做,写了个循环又整了个接口用来处理每个地址得到的数据得整合,过于繁琐.后来接粗了点RxJava,就比较容易实现了:

	<pre>
	public static Observable<List<NewsBean>> ofData(String from, String to) {  
        List<String> mList = Utils.getArrayDate(from,to);//请求的日期的集合
        Observable<NewsBean> stories = Observable.from(mList)//发射一个个日期
                .map(str -> Http.PASS_DAY_NEWS+ str)//将每个日期转化为实际请求地址
                .flatMap(Helper::getHtml)//获取每个地址的请求得到的字符串
                .flatMap(NewsListFromNetObservable::getStory);//将每个字符串转为多个实例
        return stories.toList();//将所有实例转为一个集合返回
    }
	</pre>






![Aaron Swartz](https://raw.githubusercontent.com/action-hong/MarkdownPhotos/master/pic1.PNG)
![Aaron Swartz](https://raw.githubusercontent.com/action-hong/MarkdownPhotos/master/pic2.PNG)
