/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */
package org.jasig.portal.groups.local.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.RDBMServices;
import org.jasig.portal.groups.GroupsException;
import org.jasig.portal.groups.local.ITypedEntitySearcher;

/**
 * Searches the portal DB for channels.  Used by EntitySearcherImpl
 *
 * @author Alex Vigdor
 * @version $Revision$
 */


public class RDBMChannelDefSearcher implements ITypedEntitySearcher {
    private static final Log log = LogFactory.getLog(RDBMChannelDefSearcher.class);
  private static final String is_search="select CHAN_ID from UP_CHANNEL where (UPPER(CHAN_NAME)=UPPER(?) or UPPER(CHAN_TITLE)=UPPER(?))";
  private static final String partial_search="select CHAN_ID from UP_CHANNEL where (UPPER(CHAN_NAME) like UPPER(?) or UPPER(CHAN_TITLE) like UPPER(?))";
  private Class chanDef;

  public RDBMChannelDefSearcher() {
      chanDef = org.jasig.portal.ChannelDefinition.class;
  }
  public EntityIdentifier[] searchForEntities(String query, int method) throws GroupsException {
    //System.out.println("searching for channel");
    EntityIdentifier[] r = new EntityIdentifier[0];
    ArrayList ar = new ArrayList();
    Connection conn = null;
    PreparedStatement ps = null;

        try {
            conn = RDBMServices.getConnection();
            switch(method){
              case IS:
                ps = conn.prepareStatement(RDBMChannelDefSearcher.is_search);
                break;
              case STARTS_WITH:
                query = query+"%";
                ps = conn.prepareStatement(RDBMChannelDefSearcher.partial_search);
                break;
              case ENDS_WITH:
                query = "%"+query;
                ps = conn.prepareStatement(RDBMChannelDefSearcher.partial_search);
                break;
              case CONTAINS:
                query = "%"+query+"%";
                ps = conn.prepareStatement(RDBMChannelDefSearcher.partial_search);
                break;
              default:
                throw new GroupsException("Unknown search type");
            } 
            try {
            	ps.clearParameters();
            	ps.setString(1,query);
            	ps.setString(2,query);
            	ResultSet rs = ps.executeQuery();
            	try {
            		//System.out.println(ps.toString());
            		while (rs.next()){
            			//System.out.println("result");
            			ar.add(new EntityIdentifier(rs.getString(1),chanDef));
            		} 
            	} finally {
            		close(rs);	
            		}
            } finally {
            	close(ps);
            }
        } catch (Exception e) {
            log.error("RDBMChannelDefSearcher.searchForEntities(): " + ps, e);
        } finally {
            RDBMServices.releaseConnection(conn);
        }
      return (EntityIdentifier[]) ar.toArray(r);
  }
  
  public Class getType() {
    return chanDef;
  }
  private static final void close(final Statement statement) {
	  try {
		  statement.close();
	  } catch (SQLException e) {
		  log.warn("problem closing statement", e);
	  }
  }
  private static final void close(final ResultSet resultset) {
	  try {
		  resultset.close();
	  } catch (SQLException e) {
		  log.warn("problem closing resultset", e);
	  }
  }
}