package tmt.stackoverflow;

/***
 * @Id:6
@PostTypeId:1
@AcceptedAnswerId:31
@CreationDate:2008-07-31T22:08:08.620
@Score:249
@ViewCount:16006
@Body:<p>I have an absolutely positioned <code>div</code> containing several children, one of which is a relatively positioned <code>div</code>. When I use a <strong>percentage-based width</strong> on the child <code>div</code>, it collapses to '0' width on <a href="http://en.wikipedia.org/wiki/Internet_Explorer_7" rel="noreferrer">Internet&nbsp;Explorer&nbsp;7</a>, but not on Firefox or Safari.</p> <p>If I use <strong>pixel width</strong>, it works. If the parent is relatively positioned, the percentage width on the child works.</p> <ol> <li>Is there something I'm missing here?</li> <li>Is there an easy fix for this besides the <em>pixel-based width</em> on the child?</li> <li>Is there an area of the CSS specification that covers this?</li> </ol>
@OwnerUserId:9
@LastEditorUserId:63550
@LastEditorDisplayName:Rich B
@LastEditDate:2016-03-19T06:05:48.487
@LastActivityDate:2016-03-19T06:10:52.170
@Title:Percentage width child element in absolutely positioned parent on Internet Explorer 7
@Tags:<html><css><css3><internet-explorer-7>
@AnswerCount:5
@CommentCount:0
@FavoriteCount:10
-
row
@Id:7
@PostTypeId:2
@ParentId:4
@CreationDate:2008-07-31T22:17:57.883
@Score:395
@Body:<p>An explicit cast to double like this isn't necessary:</p> <pre><code>double trans = (double) trackBar1.Value / 5000.0; </code></pre> <p>Identifying the constant as <code>5000.0</code> (or as <code>5000d</code>) is sufficient:</p> <pre><code>double trans = trackBar1.Value / 5000.0; double trans = trackBar1.Value / 5000d; </code></pre>
@OwnerUserId:9
@LastEditorUserId:4020527
@LastEditDate:2017-12-16T05:06:57.613
@LastActivityDate:2017-12-16T05:06:57.613
@CommentCount:0
 * @author user
 *
 */
public class Post {

}
