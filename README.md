GFExpandableRecyclerView
===========

GFExpandableRecyclerView was inspired by ExpandableListAdapter to assist in creating a similar experience with RecyclerView

See a short demo here:

[![Sample Video](http://img.youtube.com/vi/vJCAJXuApUE/0.jpg)](https://www.youtube.com/watch?v=vJCAJXuApUE)

Included in this library is a DividerItemDecoration to handle VERTICAL and HORIZONTAL dividers in your RecyclerViews.
```java
// Use custom divider
recyclerView.addItemDecoration(new DividerItemDecoration(drawable, orientation);
// or
recyclerView.addItemDecoration(new DividerItemDecoration(context, R.drawable.your_divider, orientation);
```
```java
// Use internal divider specified in your styles, android:listDivider
recyclerView.addItemDecoration(new DividerItemDecoration(context, orientation);
```

#### Basic Example
```java
public class GroupItem {

    List<ChildItem> mChildren;
    public List<ChildItem> getChildren() {
        return mChildren;
    }
    ...
}
```
```java
public class ChildItem {
    ...
}
```
```java
public class GroupViewHolder extends RecyclerView.ViewHolder {

    public GroupViewHolder(View v) {
        super(v);
        ...
    }
}
```
```java
public class ChildViewHolder extends RecyclerView.ViewHolder {

    public ChildViewHolder(View v) {
        super(v);
        ...
    }
}
```
```java
public class SimpleExpandableAdapter extends GFExpandableRecyclerViewAdapter<GroupItem, ChildItem, GroupViewHolder, ChildViewHolder> {

    private List<GroupItem> mGroupItems;

    public SimpleExpandableAdapter(List<GroupItem> groupItems) {
        mGroupItems = groupItems;
    }

    @Override
    public List<GroupItem> getGroupItems() {
        return mGroupItems;
    }

    @Override
    public int getGroupCount() {
        return mGroupItems.size();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return mGroupItems.get(groupPosition).getChildren().size();
    }

    @Override
    public GroupItem getGroupItem(int groupPosition) {
        return mGroupItems.get(groupPosition);
    }

    @Override
    public ChildItem getChildItem(int groupPosition,
                                  int childPosition) {
        return mGroupItems.get(groupPosition).getChildren().get(childPosition);
    }

    @Override
    public boolean isGroupInitiallyExpanded(int groupPosition) {
        return false;
    }

    @Override
    public GroupViewHolder onCreateGroupViewHolder(ViewGroup parent,
                                                   int viewType) {
        return new GroupViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item, parent, false));
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup parent,
                                                   int viewType) {
        return new ChildViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.child_item, parent, false));
    }

    @Override
    public void onBindGroupViewHolder(GroupViewHolder holder,
                                      int groupPosition,
                                      GroupItem groupItem,
                                      boolean isExpanded) {
        // set group values
    }

    @Override
    public void onBindChildViewHolder(ChildViewHolder holder,
                                      int groupPosition,
                                      int childPosition,
                                      ChildItem verticalChildItem) {
        // set child values
    }
}
```

Item Animators:
----------------

A BaseItemAnimator class is included to assist in item animation. By default, a FadeInItemAnimator will be used, however,
you may create your own animator by extending the BaseItemAnimator, and implementing your own animations. See FadeInItemAnimator
for an example of this or please refer to https://github.com/wasabeef/recyclerview-animators to see examples as this was taken from
this example.

```java
public class SampleItemAnimator extends BaseItemAnimator {

    public SampleItemAnimator() {
    }

    public SampleItemAnimator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    @Override
    protected void preAnimateRemoveImpl(RecyclerView.ViewHolder holder) {
        // do pre remove animation
    }

    @Override
    protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
        // do pre add animation
    }

    @Override
    protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        // do remove animation
    }

    @Override
    protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
        // do add animation
    }
}
```
```java
recyclerView.setItemAnimator(new SampleItemAnimator());
// or
recyclerView.setItemAnimator(new SampleItemAnimator(new Interpolator(1f)));
```

Callback Methods:
----------------
```java
/**
 * OnGroupExpandCollapseListener
 */
boolean isGroupExpandable(int groupPosition); // tell adapter if this group should be initially expanded
void onGroupExpand(int groupPosition); // callback when a group is expanded
void onGroupCollapse(int groupPosition); // callback when a group is collapsed

/**
 * OnGroupClickListener, Callback when group item is clicked
 */
void onGroupClick(GFExpandableRecyclerViewAdapter adapter,
                  View v,
                  int groupPosition);

/**
 * OnChildClickListener, Callback when child item is clicked
 */
void onChildClick(GFExpandableRecyclerViewAdapter adapter,
                  View v,
                  int groupPosition,
                  int childPosition);
```

Installation:
------------

### Directly include source into your projects

- Simply copy the source/resource files from the library folder into your project.

### Use binary approach

- Follow these steps to include aar binary in your project:

    1: Copy com.github.gfranks.expandablerecyclerview-1.0.aar into your projects libs/ directory.

    2: Include the following either in your top level build.gradle file or your module specific one:
    ```
      repositories {
         flatDir {
             dirs 'libs'
         }
     }
    ```
    3: Under your dependencies for your main module's build.gradle file, you can reference that aar file like so:
    ```compile 'com.github.gfranks.expandablerecyclerview:com.github.gfranks.expandablerecyclerview-1.0@aar'```

License
-------
Copyright (c) 2015 Garrett Franks. All rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.