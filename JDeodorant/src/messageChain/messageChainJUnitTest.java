package messageChain;
import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IBufferChangedListener;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import gr.uom.java.jdeodorant.refactoring.views.MessageChain;
import gr.uom.java.jdeodorant.refactoring.views.MessageChain.ViewContentProvider;
import gr.uom.java.jdeodorant.refactoring.views.MessageChainStructure;
public class messageChainJUnitTest {

   public ViewContentProvider makeViewContentProvider() {
      MessageChain msgChain = new MessageChain();
      return msgChain.new ViewContentProvider();
   }
   
   @Test
    public void testgetChildren() {
      MessageChainStructure parent = new MessageChainStructure("ParentClass");
      MessageChainStructure child = new MessageChainStructure(15, parent, "A().B().C()");
      assertTrue(parent.addChild(child));
      
      Object[] result = makeViewContentProvider().getChildren(parent);
      
      Object[] result2 = makeViewContentProvider().getChildren(null);
      Object[] result3 = makeViewContentProvider().getChildren("String");
      
       assertTrue(((MessageChainStructure) result[0]).getName()=="A().B().C()");
       assertTrue(((MessageChainStructure) result[0]).getStart()==15);
       assertTrue(result2.length == 0);
       assertTrue(result3.length == 0);
      
   }
   
   @Test
   public void testgetParent() {
      MessageChainStructure parent = new MessageChainStructure("ParentClass");
      MessageChainStructure child = new MessageChainStructure(15, parent, "A().B().C()");
      assertTrue(parent.addChild(child));
      
      Object result = makeViewContentProvider().getParent(child);
      
       assertTrue(((MessageChainStructure) result).getName()=="ParentClass");
       assertTrue(((MessageChainStructure) result).getStart()==-1);
   }
   
   @Test
   public void testhasChildren() {
      MessageChainStructure parent = new MessageChainStructure("ParentClass");
      MessageChainStructure child = new MessageChainStructure(15, parent, "A().B().C()", 4);
      assertTrue(parent.addChild(child));
      
      boolean resultTrue = makeViewContentProvider().hasChildren(parent);
      boolean resultFalse = makeViewContentProvider().hasChildren(child);
      Object[] children = makeViewContentProvider().getChildren(parent);
      int length = ((MessageChainStructure)children[0]).getLength();
      parent.removeChild(child);
      int size = parent.getSize();
       assertTrue(resultTrue);
       assertFalse(resultFalse);
       assertTrue(length == 4);
       assertTrue(size == 0);
   }
   
   @Test
   public void testgetElement() {
      MessageChainStructure parent = new MessageChainStructure("ParentClass");
      MessageChainStructure child = new MessageChainStructure(15, parent, "A().B().C()");
      assertTrue(parent.addChild(child));
      MessageChainStructure[] target = new MessageChainStructure[1];
      target[0] = parent;
      MessageChain msgChain = new MessageChain();
      ViewContentProvider contentProvider =msgChain.new ViewContentProvider();
      msgChain.targets = target;
      Object[] result = contentProvider.getElements(parent);
      
      assertTrue(((MessageChainStructure) result[0]).getName()=="ParentClass");
      assertTrue(((MessageChainStructure) result[0]).getStart()==-1);
   }
   
   @Test
      public void testmakeNewMethodCode () {
        MessageChain msgChain = new MessageChain();
         List<String> stringofArgumentType = new ArrayList<String>();
         stringofArgumentType.add("int");
         stringofArgumentType.add("double");
         stringofArgumentType.add("int");
         
         List<Integer> numOfArgumentOfEachMethod = new ArrayList<Integer>();
         numOfArgumentOfEachMethod.add(2);
         numOfArgumentOfEachMethod.add(1);
         
         List<String> stringOfMethodInvocation = new ArrayList<String>();
         stringOfMethodInvocation.add("method1");
         stringOfMethodInvocation.add("method2");
         
         String result = msgChain.makeNewMethodCode ("newMethod", "int", stringofArgumentType, numOfArgumentOfEachMethod, stringOfMethodInvocation);
         System.out.println(result);
         assertTrue(result.equals("public int newMethod(int x0, double x1, int x2) {\r\n" + 
               "\treturn method1(x0, x1).method2(x2);\r\n" + 
               "}\r\n"));
      }
   
   @Test
      public void testgetClassName () {
        MessageChain msgChain = new MessageChain();
         String str1 = "homework5.simple";
         String str2 = "int";
         String str3 = "csed.homwork5.simple";
         
         int length1 = str1.length();
         int length2 = str2.length();
         int length3 = str3.length();
         
         String result1 = msgChain.getClassName(length1, str1);
         String result2 = msgChain.getClassName(length2, str2);
         String result3 = msgChain.getClassName(length3, str3);
         
         assertTrue(result1.equals("simple"));
         assertTrue(result2.equals("int"));
         assertTrue(result3.equals("simple"));
      }
   
   @Test
   public void testmakeNewRefactorCode () {
      MessageChain msgChain = new MessageChain();
         List<String> stringofArgument = new ArrayList<String>();
         stringofArgument.add("x0");
         stringofArgument.add("x1");
         stringofArgument.add("x2");
         
         String result = msgChain.makeNewRefactorCode ("newMethod", stringofArgument);
         assertTrue(result.equals("newMethod(x0, x1, x2)"));
      }
   
   @Test
   public void testgetModifyPosition() {
      MessageChain msgChain = new MessageChain();
      IBuffer buffer = new IBuffer() {

         public void addBufferChangedListener(IBufferChangedListener arg0) {
            // TODO Auto-generated method stub
            
         }

         public void append(char[] arg0) {
            // TODO Auto-generated method stub
            
         }

         public void append(String arg0) {
            // TODO Auto-generated method stub
            
         }

         public void close() {
            // TODO Auto-generated method stub
            
         }

         public char getChar(int arg0) {
            if (arg0 == 2) {
               return '}';
            } else {
               return ' ';
            }
         }

         public char[] getCharacters() {
            // TODO Auto-generated method stub
            return null;
         }

         public String getContents() {
            // TODO Auto-generated method stub
            return null;
         }

         public int getLength() {
            return 5;
         }

         public IOpenable getOwner() {
            // TODO Auto-generated method stub
            return null;
         }

         public String getText(int arg0, int arg1) throws IndexOutOfBoundsException {
            // TODO Auto-generated method stub
            return null;
         }

         public IResource getUnderlyingResource() {
            // TODO Auto-generated method stub
            return null;
         }

         public boolean hasUnsavedChanges() {
            // TODO Auto-generated method stub
            return false;
         }

         public boolean isClosed() {
            // TODO Auto-generated method stub
            return false;
         }

         public boolean isReadOnly() {
            // TODO Auto-generated method stub
            return false;
         }

         public void removeBufferChangedListener(IBufferChangedListener arg0) {
            // TODO Auto-generated method stub
            
         }

         public void replace(int arg0, int arg1, char[] arg2) {
            // TODO Auto-generated method stub
            
         }

         public void replace(int arg0, int arg1, String arg2) {
            // TODO Auto-generated method stub
            
         }

         public void save(IProgressMonitor arg0, boolean arg1) throws JavaModelException {
            // TODO Auto-generated method stub
            
         }

         public void setContents(char[] arg0) {
            // TODO Auto-generated method stub
            
         }

         public void setContents(String arg0) {
            // TODO Auto-generated method stub
            
         }};

         int pos = msgChain.getModifyPosition(buffer);
         assertTrue(pos==2);
   }

}