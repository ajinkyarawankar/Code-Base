#include <bits/stdc++.h> 
using namespace std; 
  
int main() 
{ 
    // added the two lines below 
    ios_base::sync_with_stdio(false); 
    cin.tie(NULL); 
    int t,n,ans;
    cin>>t;
    while(t--){
        ans = 0;
        cin>>n;
        vector<int> a(n);
        for(int i=0;i<n;i++){
            cin>>a[i];
        }
        for(int i=0;i<n-1;i++){
            int mn = min(a[i],a[i+1]);
                int mx = max(a[i],a[i+1]);
            if(((mx*1.0)/mn)>2){
                
                while(mn<mx && ((mx*1.0)/mn)>2){
                    mn = mn*2;
                    ans++;
                }
            }
        }
        cout<<ans<<"\n";
    }
}