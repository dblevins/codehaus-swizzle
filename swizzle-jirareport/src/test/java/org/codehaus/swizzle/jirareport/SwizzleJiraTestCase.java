package org.codehaus.swizzle.jirareport;

import junit.framework.TestCase;
import org.codehaus.swizzle.jira.JiraXmlRpc;

public abstract class SwizzleJiraTestCase
    extends TestCase
{
    protected JiraXmlRpc getJira()
        throws Exception
    {
        JiraXmlRpc jira = new JiraXmlRpc( "http://jira.codehaus.org/rpc/xmlrpc" );
        jira.login( "swizzle", "swizzle" );
        return jira;
    }
}
