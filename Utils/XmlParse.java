/*
Pull解析方式
 */

private void parseXMLWithPull(String xmlData){
	try{
		XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
		XmlPullParse xmlPullParse=factory.newPullParser();
		xmlPullParse.setInput(new StringReader(xmlData));
		int eventType=xmlPullParse.getEventType();
		String id="";
		String name="";
		String version="";

		while(eventType!=XmlPullParse.END_DOCUMENT){
			String nodeName=xmlPullParse.getName();
			switch(eventType){
				//开始解析某个节点
				case XmlPullParse.START_TAG:{
					if("id".equals(nodeName)){
						id=xmlPullParse.nextText();
					}else if("name".equals(nodeName)){
						name=xmlPullParse.nextText();
					}else if("version".equals(nodeName)){
						version=xmlPullParse.nextText();
					}
					break;
					}
				case XmlPullParse.END_TAG:{
					if("app".equals(nodeName)){
						Log.d("MainActivity","id is"+id);
						Log.d("MainActivity","name is"+name);
						Log.d("MainActivity","version is"+version);
					}
					break;
				}
				default:
				break;
			}
			eventType=xmlPullParse.next();
		}
	}catch(Exception e){
		e.printStackTrace();
	}
}
/*
SAX解析方式
 */

public class MyHandler extends DefaultHandler{
	@Override
	public void startDocument() throws SAXException{

	}
	@Override
	public void startElement(String uri,String localName,String qName,
		Attributes attributes) throws SAXException{

	}
	@Override
	public void characters(char[] ch,int start,int length) throws SAXException{

	}
	@Override
	public void endElement(String uri,String localName,String qName) throws
	SAXException{

	}
	@Override
	public void endDocument() throws SAXException{

	}
}

SAXParserFactory factory=SAXParserFactory.netInstance();
XMLReader xmlReader=factory.newSAXParser().getXMLReader();
ContentHandler handler=new ContentHandler();
//将ContentHandler的实例设置到XMLReader中
xmlReader.setContentHandler(handler);
//开始执行解析
xmlReader.parse(new InputSource(new StringReader(xmlData)));