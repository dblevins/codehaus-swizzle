package org.codehaus.swizzle.jira;

import junit.framework.TestCase;

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
