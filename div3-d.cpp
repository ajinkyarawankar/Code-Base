#include <bits/stdc++.h> 
using namespace std; 
vector<int> ans;
vector<int> a;

void dfs(int index, int left, int right, int depth){
    int mx;
    if(left<index){
        mx = left;
        for(int i=left;i<index;i++){
            if(a[mx]<a[i]) mx = i;
        }
        ans[mx] = depth;
        dfs(mx, left, index-1, depth+1);
    }
    if(right>index){
        mx = index+1;
        for(int i=index+1;i<=right;i++){
            if(a[mx]<a[i]) mx = i;
        }
        ans[mx] = depth;
        dfs(mx, index+1, right, depth+1);
    }
}

int main() 
{ 
    // added the two lines below 
    ios_base::sync_with_stdio(false); 
    cin.tie(NULL); 
    int t,n;
    cin>>t;
    while(t--){
        cin>>n;
        a.resize(n,0);
        ans.resize(n,0);
        int mx = 0;
        for(int i=0;i<n;i++){
           cin>>a[i];
           if(a[i]==n) mx = i;
        }
        ans[mx]=0;
        dfs(mx,0,n-1,1);
        for(int i=0;i<n;i++){
            cout<<ans[i]<<" ";
        }
        cout<<"\n";
    }
}